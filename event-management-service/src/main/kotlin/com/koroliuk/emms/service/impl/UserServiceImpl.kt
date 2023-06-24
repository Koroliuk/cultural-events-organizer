package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.*
import com.koroliuk.emms.model.complaint.CommentComplaint
import com.koroliuk.emms.model.complaint.Complaint
import com.koroliuk.emms.model.complaint.ComplaintStatus
import com.koroliuk.emms.model.complaint.EventComplaint
import com.koroliuk.emms.model.user.SavedEvent
import com.koroliuk.emms.model.user.SavedGroup
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.repository.complaint.CommentComplaintRepository
import com.koroliuk.emms.repository.complaint.ComplaintRepository
import com.koroliuk.emms.repository.complaint.EventComplaintRepository
import com.koroliuk.emms.repository.event.EventRepository
import com.koroliuk.emms.repository.group.CommentRepository
import com.koroliuk.emms.repository.group.GroupSubscriptionRepository
import com.koroliuk.emms.repository.user.SavedEventRepository
import com.koroliuk.emms.repository.user.SavedGroupRepository
import com.koroliuk.emms.repository.user.UserRepository
import com.koroliuk.emms.repository.user.VolunteerApplicationRepository
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ConversionUtils.convertVolunteerApplicationForResponse
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime


@Singleton
class UserServiceImpl (
    @Inject private val userRepository: UserRepository,
    @Inject private val savedEventRepository: SavedEventRepository,
    @Inject private val savedGroupRepository: SavedGroupRepository,
    @Inject private val eventRepository: EventRepository,
    @Inject private val groupSubscriptionRepository: GroupSubscriptionRepository,
    @Inject private val complaintRepository: ComplaintRepository,
    @Inject private val eventComplaintRepository: EventComplaintRepository,
    @Inject private val commentComplaintRepository: CommentComplaintRepository,
    @Inject private val commentRepository: CommentRepository,
    @Inject private val volunteerApplicationRepository: VolunteerApplicationRepository
) : UserService {

    override fun create(user: User) {
        val username = user.username
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("User with email $username already exists")
        }
        userRepository.save(user)
    }

    override fun update(user: User) {
        userRepository.update(user)
    }

    override fun existByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    override fun saveEvent(eventId: Long, user: User): SavedEvent {
        val savedEvent = SavedEvent(
            user = user,
            event = eventRepository.findById(eventId).get(),
            createdDate = LocalDateTime.now()
        )
        savedEventRepository.save(savedEvent)
        return savedEvent
    }

    override fun findSavedEventById(id: Long): SavedEvent? {
        val optionalSavedEvent = savedEventRepository.findById(id)
        if (optionalSavedEvent.isEmpty) {
            return null
        }
        return optionalSavedEvent.get()    }

    override fun getAllSavedEventsByUser(page: Int, size: Int, user: User): GetSavedEventsResponse {
        val pageable = Pageable.from(page, size)
        val savedEventPage = savedEventRepository.findAllByUserOrderByCreatedDateDesc(user, pageable)
        return GetSavedEventsResponse(
            page = savedEventPage.pageable.number,
            size = savedEventPage.pageable.size,
            totalSize = savedEventPage.totalSize,
            totalPage = savedEventPage.totalPages,
            content = savedEventPage.content.stream()
                .map { SaveEventResponse(it.id!!, it.event.id!!) }
                .toList()
        )
    }

    override fun isEventSavedByUser(eventId: Long, user: User): Boolean {
        return savedEventRepository.existsByEventIdAndUser(eventId, user)
    }

    override fun deleteSavedEventById(id: Long) {
        savedEventRepository.deleteById(id)
    }

    override fun findSavedGroupById(id: Long): SavedGroup? {
        val optionalSavedGroup = savedGroupRepository.findById(id)
        if (optionalSavedGroup.isEmpty) {
            return null
        }
        return optionalSavedGroup.get()
    }

    override fun isGroupSavedByUser(groupId: Long, user: User): Boolean {
        return savedGroupRepository.existsByGroupIdAndUser(groupId, user)
    }

    override fun getAllSavedGroupsByUser(page: Int, size: Int, user: User): GetSavedGroupsResponse {
        val pageable = Pageable.from(page, size)
        val savedGroupsPage = savedGroupRepository.findAllByUserOrderByCreatedDateDesc(user, pageable)
        return GetSavedGroupsResponse(
            page = savedGroupsPage.pageable.number,
            size = savedGroupsPage.pageable.size,
            totalSize = savedGroupsPage.totalSize,
            totalPage = savedGroupsPage.totalPages,
            content = savedGroupsPage.content.stream()
                .map { SaveGroupResponse(it.id!!, it.group.id!!) }
                .toList()
        )
    }

    override fun deleteSavedGroupById(id: Long) {
        savedGroupRepository.deleteById(id)
    }

    override fun isSubcribetToThisGroup(groupId: Long, user: User): Boolean {
        return groupSubscriptionRepository.existsByUserAndGroupId(user, groupId)
    }

    override fun deleteSubscriptionByGroupIdAndUser(groupId: Long, user: User) {
        groupSubscriptionRepository.deleteByGroupIdAndUser(groupId, user)
    }

    override fun complianAboutEvent(eventId: Long, user: User) {
        val complaint = Complaint(
            author = user,
            reason = null,
            status = ComplaintStatus.REPORTED
        )
        val saved = complaintRepository.save(complaint)
        val eventComplaint = EventComplaint(
            complaint = saved,
            event = eventRepository.findById(eventId).orElse(null)
        )
        eventComplaintRepository.save(eventComplaint)
    }

    override fun complianAboutComment(eventId: Long, user: User) {
        val complaint = Complaint(
            author = user,
            reason = null,
            status = ComplaintStatus.REPORTED
        )
        val saved = complaintRepository.save(complaint)
        val eventComplaint = CommentComplaint(
            complaint = saved,
            comment = commentRepository.findById(eventId).get()
        )
        commentComplaintRepository.save(eventComplaint)
    }

    override fun getUserComplaints(page: Int, size: Int, user: User): GetComplaintsResponse {
        val pageable = Pageable.from(page, size)
        val complaintsPage = complaintRepository.findInfoByAuthor(user.username, pageable)
        return GetComplaintsResponse(
            page = complaintsPage.pageable.number,
            size = complaintsPage.size,
            totalSize = complaintsPage.totalSize,
            totalPage = complaintsPage.totalPages,
            content = complaintsPage.content
        )
    }

    override fun getUserVolunteerApplications(user: User, page: Int, size: Int): GetVolunteerApplicationsResponse {
        val pageable = Pageable.from(page, size)
        val volunteerApplicationsPage = volunteerApplicationRepository.findAllByUser(user, pageable)
        return GetVolunteerApplicationsResponse(
            page = volunteerApplicationsPage.pageable.number,
            size = volunteerApplicationsPage.size,
            totalSize = volunteerApplicationsPage.totalSize,
            totalPage = volunteerApplicationsPage.totalPages,
            content = volunteerApplicationsPage.content.stream()
                .map { convertVolunteerApplicationForResponse(it) }
                .toList()
        )
    }

    override fun getUserSubscriptions(page: Int, size: Int, user: User): GetGroupSubscriptionsResponse {
        val pageable = Pageable.from(page, size)
        val groupSubscriptionsPage = groupSubscriptionRepository.findAllByUser(user, pageable)
        return GetGroupSubscriptionsResponse(
            page = groupSubscriptionsPage.pageable.number,
            size = groupSubscriptionsPage.size,
            totalSize = groupSubscriptionsPage.totalSize,
            totalPage = groupSubscriptionsPage.totalPages,
            content = groupSubscriptionsPage.content.stream()
                .map { GroupSubscriptionResponse(it.id!!, it.group.id!!) }
                .toList()
        )
    }

}
