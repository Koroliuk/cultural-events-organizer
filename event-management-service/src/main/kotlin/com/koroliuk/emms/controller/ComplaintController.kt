package com.koroliuk.emms.controller

import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE_SIZE
import com.koroliuk.emms.utils.ControllerUtils.getCurrentUser
import com.koroliuk.emms.controller.request.CreateComplaintRequest
import com.koroliuk.emms.controller.response.GetComplaintsResponse
import com.koroliuk.emms.model.complaint.ComplaintStatus
import com.koroliuk.emms.model.user.ADMIN
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.ComplaintService
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.GroupService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal

@Controller("/api/complaints")
class ComplaintController(
    @Inject private val complaintService: ComplaintService,
    @Inject private val eventService: EventService,
    @Inject private val userService: UserService,
    @Inject private val groupService: GroupService
) {

    @Get
    @Secured(ADMIN)
    fun getAllComplaints(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        @QueryValue(defaultValue = "REPORTED") status: ComplaintStatus?
    ): HttpResponse<GetComplaintsResponse> {
        val complaintInfosPage = complaintService.getAllComplaintInfos(page, size, status)
        return HttpResponse.ok(complaintInfosPage)
    }

    @Get("/{id}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun getComplaintById(id: Long, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val complaint = complaintService.findComplaintInfoById(id) ?: return HttpResponse.badRequest(createMessageResponse("No complaint with such id"))
        var isAllowed = false
        if (user.role == ADMIN || (user.role == USER && user.username == complaint.authorUsername)) {
            isAllowed = true
        }
        if (!isAllowed) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You must be admin or author of this complaint"))
        }
        return HttpResponse.ok(complaint)
    }

    @Post
    @Secured(USER)
    fun createComplaint(
        @Body request: CreateComplaintRequest,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        return when (request.type.lowercase()) {
            EVENT_COMPLAINT_TYPE -> {
                val event = eventService.findById(request.objectId) ?: return HttpResponse.badRequest(
                    createMessageResponse("No such event")
                )
                val complaint = complaintService.complainAboutEvent(event, request.reason, user)
                HttpResponse.ok(complaint)
            }

            COMMENT_COMPLAINT_TYPE -> {
                val comment = groupService.findCommentById(request.objectId) ?: return HttpResponse.badRequest(
                    createMessageResponse("No such comment")
                )
                val complaint = complaintService.complainAboutComment(comment, request.reason, user)
                HttpResponse.ok(complaint)
            }

            else -> HttpResponse.badRequest(createMessageResponse("Invalid complaint type"))
        }
    }


    @Delete("/{id}")
    @Secured(USER)
    fun deleteComplaintById(id: Long, principal: Principal): HttpResponse<Any> {
        val complaint = complaintService.findComplaintById(id)
            ?: return HttpResponse.badRequest(createMessageResponse("No such complaint"))
        if (complaint.author.username != principal.name) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not an author of this complaint"))
        }
        complaintService.delete(complaint)
        return HttpResponse.noContent()
    }

    @Secured(ADMIN)
    @Put("/{complaintId}/reject")
    fun rejectComplaint(complaintId: Long): HttpResponse<Any> {
        val complaint = complaintService.findComplaintById(complaintId) ?:
            return HttpResponse.badRequest(createMessageResponse("No such complaint"))
        complaintService.reject(complaint)
        return HttpResponse.ok(createMessageResponse("Complaint rejected"))
    }

    @Secured(ADMIN)
    @Put("/{complaintId}/approve")
    fun approveComplaint(complaintId: Long): HttpResponse<Any> {
        val complaint = complaintService.findComplaintById(complaintId) ?:
        return HttpResponse.badRequest(createMessageResponse("No such complaint"))
        complaintService.approve(complaint)
        return HttpResponse.ok(createMessageResponse("Complaint successfully approved"))
    }

    companion object {

        const val EVENT_COMPLAINT_TYPE = "event"
        const val COMMENT_COMPLAINT_TYPE = "comment"

    }

}
