package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.dto.*
import com.koroliuk.emms.controller.response.*
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventType
import com.koroliuk.emms.model.group.*
import com.koroliuk.emms.model.notification.NotificationType
import com.koroliuk.emms.model.user.SavedGroup
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.repository.complaint.CommentComplaintRepository
import com.koroliuk.emms.repository.complaint.ComplaintRepository
import com.koroliuk.emms.repository.event.EventMediaRepository
import com.koroliuk.emms.repository.event.EventOrganizerRepository
import com.koroliuk.emms.repository.event.OfflineEventRepository
import com.koroliuk.emms.repository.event.OnlineEventRepository
import com.koroliuk.emms.repository.group.CommentRepository
import com.koroliuk.emms.repository.group.GroupManagerRepository
import com.koroliuk.emms.repository.group.GroupRepository
import com.koroliuk.emms.repository.group.GroupSubscriptionRepository
import com.koroliuk.emms.repository.user.SavedGroupRepository
import com.koroliuk.emms.service.EmailService
import com.koroliuk.emms.service.GroupService
import com.koroliuk.emms.service.NotificationService
import com.koroliuk.emms.utils.ControllerUtils
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream
import javax.transaction.Transactional


@Singleton
open class GroupServiceImpl(
    @Inject private val groupRepository: GroupRepository,
    @Inject private val groupManagerRepository: GroupManagerRepository,
    @Inject private val commentRepository: CommentRepository,
    @Inject private val complaintRepository: ComplaintRepository,
    @Inject private val commentComplaintRepository: CommentComplaintRepository,
    @Inject private val eventOrganizerRepository: EventOrganizerRepository,
    @Inject private val groupSubscriptionRepository: GroupSubscriptionRepository,
    @Inject private val savedGroupRepository: SavedGroupRepository,
    @Inject private val emailService: EmailService,
    @Inject private val notificationService: NotificationService,
    @Inject private val eventMediaRepository: EventMediaRepository,
    @Inject private val offlineEventRepository: OfflineEventRepository,
    @Inject private val onlineEventRepository: OnlineEventRepository,
) : GroupService {

    @Transactional
    override fun createGroup(name: String, user: User): GroupInfo {
        val group = Group(name = name, managers = Collections.emptyList())
        groupRepository.save(group)
        val owner = GroupManager(
            group = group,
            user = user,
            role = GroupManagerRole.OWNER
        )
        val manager = groupManagerRepository.save(owner)
        return createGroupIntoFromGroupWIthOwner(group, manager)
    }

    override fun updateGroup(group: Group, groupDto: GroupDto): GroupInfo {
        group.name = groupDto.name
        val updatedGroup = groupRepository.update(group)
        return createGroupIntoFromGroup(updatedGroup)
    }

    @Transactional
    override fun deleteGroup(id: Long) {
        groupManagerRepository.deleteByGroupId(id)
        groupSubscriptionRepository.deleteByGroupId(id)
        savedGroupRepository.deleteByGroupId(id)
        eventOrganizerRepository.deletedByGroupId(id)
        val comments = commentRepository.findByGroupId(id)
        for (comment in comments) {
            val commentComplaints = commentComplaintRepository.findAllByCommentId(comment.id!!)
            for (commentComplaint in commentComplaints) {
                val complaint = commentComplaint.complaint
                commentComplaintRepository.deleteById(commentComplaint.id!!)
                complaintRepository.deleteById(complaint.id!!)
            }
            commentRepository.deleteById(comment.id!!)
        }
        groupRepository.deleteById(id)
    }

    override fun createComment(group: Group, commentDto: CommentCreateDto, user: User): CommentDto {
        val comment = Comment(
            text = commentDto.text,
            author = user,
            group = group
        )
        commentRepository.save(comment)
        return CommentDto(comment.id!!, comment.text, comment.author.username, comment.group.id!!)
    }

    override fun isOwner(user: User, group: Group): Boolean {
        return group.managers.any { it.user.id == user.id && it.role == GroupManagerRole.OWNER }
    }

    override fun getGroupManagers(group: Group): List<User> {
        return groupManagerRepository.findAllByGroupId(group.id!!).stream()
            .map { it.user }
            .toList()
    }

    override fun existsById(id: Long): Boolean {
        return groupRepository.existsById(id)
    }

    override fun existsByName(name: String): Boolean {
        return groupRepository.existsByName(name)
    }

    override fun commentExistsById(id: Long): Boolean {
        return commentRepository.existsById(id)
    }

    override fun findById(id: Long): Group? {
        return groupRepository.findById(id).orElse(null)
    }

    override fun getGroupInfoById(id: Long): GroupInfo? {
        val group = findById(id) ?: return null
        return createGroupIntoFromGroup(group)
    }

    override fun isUserAuthorOfComment(comment: Comment, user: User): Boolean {
        return comment.author.id == user.id
    }

    override fun hideComment(comment: Comment) {
        comment.isHidden = true
        commentRepository.update(comment)
    }

    override fun findCommentById(id: Long): Comment? {
        return commentRepository.findById(id).orElse(null)
    }

    override fun findCommentsOfGroup(group: Group, page: Int, size: Int): GetCommentsResponse {
        val pageable = Pageable.from(page, size)
        val resultPage = commentRepository.findByGroupIdAndHidden(group.id!!, false, pageable)
        return GetCommentsResponse(
            page = resultPage.pageable.number,
            size = resultPage.size,
            totalSize = resultPage.totalSize,
            totalPage = resultPage.totalPages,
            content = resultPage.content.stream()
                .map {
                    CommentResponse(
                        id = it.id!!,
                        text = it.text,
                        username = it.author.username,
                        groupId = it.group.id!!,
                        replyTo = it.replyTo
                    )
                }
                .toList()
        )

    }

    override fun findGroupEvents(group: Group, page: Int, size: Int): GetEventInfoResponse {
        val pageable = Pageable.from(page, size)
        val eventOrganizers = eventOrganizerRepository.findByGroupAndEventBlocked(group, false, pageable)
        return GetEventInfoResponse(
            page = eventOrganizers.pageable.number,
            size = eventOrganizers.size,
            totalSize = eventOrganizers.totalSize,
            totalPage = eventOrganizers.totalPages,
            content = eventOrganizers.content
                .mapNotNull { createEventInfo(it.event) }
        )
    }

    fun createEventInfo(event: Event): EventInfo? {
        val eventType: EventType
        val location: String?
        val url: String?

        val eventMedia = eventMediaRepository.findAllByEventId(event.id!!)
        val onlineEvent = onlineEventRepository.findByEventId(event.id!!)
        val offlineEvent = offlineEventRepository.findByEventId(event.id!!)

        if (offlineEvent != null) {
            eventType = EventType.OFFLINE
            location = offlineEvent.location
            url = null
        } else if (onlineEvent != null) {
            eventType = EventType.ONLINE
            location = null
            url = onlineEvent.url
        } else {
            return null
        }

        return EventInfo(
            id = event.id!!,
            category = event.category.name,
            name = event.name,
            description = event.description,
            startTime = event.startTime.format(ControllerUtils.formatter),
            endTime = event.endTime.format(ControllerUtils.formatter),
//            eventType = eventType,
//            location = location,
//            url = url,
            eventType = EventType.ONLINE,
            location = "location",
            url = "url",
            visibilityType = event.visibilityType,
            eventMedia = eventMedia
        )
    }

    override fun findGroupSubscribers(group: Group, page: Int, size: Int): GetGroupSubscriptionsResponse {
        val pageable = Pageable.from(page, size)
        val result = groupSubscriptionRepository.findAllByGroupId(group.id!!, pageable)
        return GetGroupSubscriptionsResponse(
            page = result.pageable.number,
            size = result.size,
            totalSize = result.totalSize,
            totalPage = result.totalPages,
            content = result.content.stream()
                .map {
                    GroupSubscriptionResponse(
                        id = it.id!!,
                        groupId = it.group.id!!,
                        username = it.user.username
                    )
                }
                .toList()
        )
    }

    override fun addManager(group: Group, newManager: User): GroupInfo {
        var isAlreadyAdded = false
        for (groupManager in group.managers) {
            if (groupManager.user.username == newManager.username) {
                isAlreadyAdded = true
            }
        }
        if (!isAlreadyAdded) {
            val groupManager = GroupManager(user = newManager, group = group, role = GroupManagerRole.MANAGER)
            groupManagerRepository.save(groupManager)
            val updatedGroup = groupRepository.findById(group.id!!).orElse(null)
            return createGroupIntoFromGroup(updatedGroup)
        }
        return createGroupIntoFromGroup(group)
    }

    override fun deleteManager(group: Group, managerId: Long): GroupInfo {
        val groupManager = groupManagerRepository.findById(managerId).get()
        groupManagerRepository.delete(groupManager)
        val groupNew = groupRepository.findById(group.id!!).get()
        return createGroupIntoFromGroup(groupNew)
    }

    override fun subcribeToGroup(groupId: Long, user: User): GroupSubscription {
        val obj = GroupSubscription(
            user = user,
            group = findById(groupId)!!
        )
        return groupSubscriptionRepository.save(obj)
    }

    override fun saveGroup(groupId: Long, user: User): SavedGroup {
        val savedGroup = SavedGroup(
            user = user,
            group = findById(groupId)!!,
            createdDate = LocalDateTime.now()
        )
        savedGroupRepository.save(savedGroup)
        return savedGroup
    }

    override fun announce(groudId: Long, announceDto: AnnounceDto) {
        val group = groupRepository.findById(groudId).orElseThrow { IllegalArgumentException("Group not found") }
        groupManagerRepository.findAllByGroupId(groudId).stream()
            .map { it.user }
            .forEach {
                if (it.isSubscribedFor) {
                    emailService.sendMessageToQueue(announceDto.message, it.email)
                }
                notificationService.addNotificationForUser(it, announceDto.message, NotificationType.INFO, null, group)
            }
    }

    override fun getGroupByUser(user: User, page: Int, size: Int): GetGroupResponse {
        val pageable = Pageable.from(page, size)
        val result = groupManagerRepository.findAllByUser(user, pageable)
        return GetGroupResponse(
            page = result.pageable.number,
            size = result.size,
            totalSize = result.totalSize,
            totalPage = result.totalPages,
            content = result.content.stream()
                .map { createGroupIntoFromGroup(it.group) }
                .toList()
        )
    }

    private fun createGroupIntoFromGroupWIthOwner(group: Group, manager: GroupManager): GroupInfo {
        return GroupInfo(
            id = group.id!!,
            name = group.name,
            isBlocked = group.isBlocked,
            managers = Stream.of(manager)
                .map { covertGroupManagerToGroupManagerInfo(it) }
                .toList()
        )
    }

    private fun createGroupIntoFromGroup(group: Group): GroupInfo {
        return GroupInfo(
            id = group.id!!,
            name = group.name,
            isBlocked = group.isBlocked,
            managers = groupManagerRepository.findAllByGroupId(group.id!!).stream()
                .map { covertGroupManagerToGroupManagerInfo(it) }
                .toList()
        )
    }

    private fun covertGroupManagerToGroupManagerInfo(groupManager: GroupManager): GroupManagerInfo {
        return GroupManagerInfo(
            id = groupManager.id!!,
            name = groupManager.user.username,
            role = groupManager.role
        )
    }
}
