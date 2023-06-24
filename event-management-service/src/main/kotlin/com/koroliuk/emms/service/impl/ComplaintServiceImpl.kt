package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.dto.ComplaintInfo
import com.koroliuk.emms.controller.response.GetComplaintsResponse
import com.koroliuk.emms.model.complaint.CommentComplaint
import com.koroliuk.emms.model.complaint.Complaint
import com.koroliuk.emms.model.complaint.ComplaintStatus
import com.koroliuk.emms.model.complaint.EventComplaint
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.group.Comment
import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.notification.NotificationType
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.repository.complaint.CommentComplaintRepository
import com.koroliuk.emms.repository.complaint.ComplaintRepository
import com.koroliuk.emms.repository.complaint.EventComplaintRepository
import com.koroliuk.emms.repository.event.EventOrganizerRepository
import com.koroliuk.emms.repository.event.EventRepository
import com.koroliuk.emms.repository.group.CommentRepository
import com.koroliuk.emms.repository.group.GroupRepository
import com.koroliuk.emms.service.*
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.stream.Collectors
import javax.transaction.Transactional

@Singleton
open class ComplaintServiceImpl(
    @Inject private val complaintRepository: ComplaintRepository,
    @Inject private val notificationService: NotificationService,
    @Inject private val groupService: GroupService,
    @Inject private val commentRepository: CommentRepository,
    @Inject private val userService: UserService,
    @Inject private val eventRepository: EventRepository,
    @Inject private val eventOrganizerRepository: EventOrganizerRepository,
    @Inject private val groupRepository: GroupRepository,
    @Inject private val eventComplaintRepository: EventComplaintRepository,
    @Inject private val commentComplaintRepository: CommentComplaintRepository
) : ComplaintService {

    @Transactional
    override fun complainAboutEvent(event: Event, reason: String, user: User): ComplaintInfo {
        val savedComplaint = saveComplaint(reason, user)
        val eventComplaint = EventComplaint(
            complaint = savedComplaint,
            event = event
        )
        eventComplaintRepository.save(eventComplaint)
        return complaintToComplaintInfo(savedComplaint, event.id!!, null)
    }

    @Transactional
    override fun complainAboutComment(comment: Comment, reason: String, user: User): ComplaintInfo {
        val savedComplaint = saveComplaint(reason, user)
        val commentComplaint = CommentComplaint(
            complaint = savedComplaint,
            comment = comment
        )
        commentComplaintRepository.save(commentComplaint)
        return complaintToComplaintInfo(savedComplaint, null, comment.id!!)
    }

    override fun reject(complaint: Complaint) {
        updateStatus(complaint.id!!, ComplaintStatus.CANCELED)
        sendNotificationToComplaintAuthor(complaint, "You complaint was rejected")
    }

    override fun approve(complaint: Complaint) {
        updateStatus(complaint.id!!, ComplaintStatus.APPROVED)
        if (complaint.commentComplaint != null) {
            handleCommentComplaint(complaint)
        }
        if (complaint.eventComplaint != null) {
            handleEventComplaint(complaint)
        }
        sendNotificationToComplaintAuthor(complaint, "You complaint was approved. Thanks!")
    }

    override fun getAllComplaintInfos(pageNumber: Int, size: Int, status: ComplaintStatus?): GetComplaintsResponse {
        val pageable = Pageable.from(pageNumber, size)
        val page = complaintRepository.findByStatus(status, pageable)
        return GetComplaintsResponse(
            page = page.pageNumber,
            size = page.size,
            totalPage = page.totalPages,
            totalSize = page.totalSize,
            content = page.content.stream()
                .map {
                    var eventId: Long? = null
                    var commentId: Long? = null
                    if (it.eventComplaint != null) {
                        eventId = it.eventComplaint!!.event.id
                    }
                    if (it.commentComplaint != null) {
                        commentId = it.commentComplaint!!.comment.id
                    }
                    ComplaintInfo(
                        authorUsername = it.author.username,
                        reason = it.reason,
                        status = it.status,
                        eventId = eventId,
                        commentId = commentId,
                        id = it.id!!
                    )
                }
                .toList()
        )
    }

    override fun getEventComplaints(id: Long, page: Int, size: Int): GetComplaintsResponse {
        val pageable = Pageable.from(page, size)
        val result = complaintRepository.findByEventIdAndStatus(id, ComplaintStatus.APPROVED, pageable)
        return GetComplaintsResponse(
            page = result.pageNumber,
            size = result.size,
            totalPage = result.totalPages,
            totalSize = result.totalSize,
            content = result.content.stream()
                    .map { ComplaintInfo(
                            authorUsername = it.author.username,
                            reason = it.reason,
                            status = it.status,
                            eventId = id,
                            commentId = null,
                            id = it.id!!
                    ) }
                    .toList()
        )
    }


    override fun findComplaintInfoById(id: Long): ComplaintInfo? {
        val result = complaintRepository.findById(id)
        if (result.isEmpty) {
            return null
        }
        return complaintToComplaintInfo(result.get(), null, null)
    }

    override fun findComplaintById(id: Long): Complaint? {
        return complaintRepository.findById(id).orElse(null)
    }

    @Transactional
    override fun delete(complaint: Complaint) {
        complaint.commentComplaint?.let {
            commentComplaintRepository.delete(it)
        }
        complaint.eventComplaint?.let {
            eventComplaintRepository.delete(it)
        }
        complaintRepository.delete(complaint)
    }

    private fun sendNotificationToComplaintAuthor(complaint: Complaint, message: String) {
        val event = getEventFromComplaint(complaint)
        val group = getGroupFromComplaint(complaint)
        notificationService.addNotificationForUser(
            complaint.author,
            message,
            NotificationType.INFO,
            event,
            group
        )
    }

    private fun handleCommentComplaint(complaint: Complaint) {
        groupService.hideComment(complaint.commentComplaint!!.comment)
        val commentAuthorComplaints = countUserCommentsWithComplaints(complaint.author.id!!)
        if (commentAuthorComplaints > 3) {
            complaint.author.isBlocked = true
            userService.update(complaint.author)
        } else {
            notificationService.addNotificationForUser(
                complaint.author,
                "More approved complaint on your comments and account will be blocked",
                NotificationType.WARNING,
                null,
                complaint.commentComplaint!!.comment.group
            )
        }
    }

    private fun handleEventComplaint(complaint: Complaint) {
        val event = eventRepository.findById(complaint.eventComplaint!!.id!!)
        if (event.isPresent) {
            event.get().blocked = true
            eventRepository.update(event.get())
            val groups = eventOrganizerRepository.findByEvent(event.get())
                .stream()
                .map { it.group }
                .collect(Collectors.toSet())
            for (group in groups) {
                notifyGroupManagers(group, event.get())
                val groupComplaintCount = complaintRepository.countGroupComplaints(group.id!!)
                if (groupComplaintCount >= 3) {
                    blockGroup(group)
                }
            }
        }
    }

    private fun blockGroup(group: Group) {
        group.isBlocked = true
        groupRepository.update(group)
    }

    private fun notifyGroupManagers(group: Group, event: Event) {
        val managers = groupService.getGroupManagers(group) // assuming getGroupManagers method exists in groupService
        for (manager in managers) {
            notificationService.addNotificationForUser(
                manager,
                "The event associated with your group is blocked",
                NotificationType.WARNING,
                event,
                group
            )
        }
    }

    private fun getEventFromComplaint(complaint: Complaint): Event? {
        val event = if (complaint.eventComplaint != null) {
            complaint.eventComplaint!!.event
        } else {
            null
        }
        return event
    }

    private fun getGroupFromComplaint(complaint: Complaint): Group? {
        val group = if (complaint.commentComplaint != null) {
            complaint.commentComplaint!!.comment.group
        } else {
            null
        }
        return group
    }

    private fun countUserCommentsWithComplaints(userId: Long): Long {
        return commentRepository.countUserCommentsWithComplaintsByStatus(userId, ComplaintStatus.APPROVED)
    }

    private fun getComplaintById(id: Long): Complaint? {
        val optionalComplaint = complaintRepository.findById(id)
        if (optionalComplaint.isEmpty) {
            return null
        }
        return optionalComplaint.get()
    }

    private fun updateStatus(complaintId: Long, status: ComplaintStatus) {
        complaintRepository.updateStatusById(complaintId, status)
    }

    private fun saveComplaint(reason: String, user: User): Complaint {
        val complaint = Complaint(
            author = user,
            reason = reason,
            status = ComplaintStatus.REPORTED,
        )
        return complaintRepository.save(complaint)
    }

    private fun complaintToComplaintInfo(complaint: Complaint, eventId: Long?, commentId: Long?): ComplaintInfo {
        return ComplaintInfo(
            authorUsername = complaint.author.username,
            reason = complaint.reason,
            status = complaint.status,
            eventId = eventId,
            commentId = commentId,
            id = complaint.id!!
        )
    }

}
