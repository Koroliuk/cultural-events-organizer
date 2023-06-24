package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.request.EventCreateRequest
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.AttendanceEntryService
import com.koroliuk.emms.service.EventFeedbackService
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import com.koroliuk.emms.utils.ControllerUtils.isManagerOfEvent
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.security.Principal
import java.time.LocalDateTime
import javax.validation.Valid


@Controller("/api/events/{eventId}/feedbacks")
@Secured(USER)
class EventFeedbackController(
        @Inject private val userService: UserService,
        @Inject private val eventFeedbackService: EventFeedbackService,
        @Inject private val attendanceEntryService: AttendanceEntryService,
        @Inject private val eventService: EventService,
) {

    @Post
    fun leaveFeedback(
            @PathVariable eventId: Long,
            @Valid @Body request: EventCreateRequest,
            principal: Principal
    ): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest(createMessageResponse("Event or one of organizers groups is blocked"))
        }
        if (isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (LocalDateTime.now() <= event.endTime) {
            return HttpResponse.badRequest(createMessageResponse("Event is not ended"))
        }
        val isAttendedEvent = attendanceEntryService.isUserAttendEvent(event, user)
        if (!isAttendedEvent) {
            return HttpResponse
                    .status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("User didn't attend this event"))
        }
        val isUserAlreadyLeftFeedback = eventFeedbackService.isUserAlreadyLeftFeedback(user, event)
        if (isUserAlreadyLeftFeedback) {
            return HttpResponse
                    .status<Any?>(HttpStatus.CONFLICT)
                    .body(createMessageResponse("User has already left feedback"))
        }
        return HttpResponse.ok(eventFeedbackService.leaveFeedback(event, request.rate, request.feedback, user))
    }

    @Get("/{feedbackId}")
    fun getEventFeedbackById(
            @PathVariable eventId: Long,
            @PathVariable feedbackId: Long,
            principal: Principal
    ): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest("There is no such event")
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest(createMessageResponse("Event or one of organizers groups is blocked"))
        }
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (event.endTime > LocalDateTime.now()) {
            return HttpResponse.badRequest(createMessageResponse("Event is not ended"))
        }
        val feedback = eventFeedbackService.findById(eventId) ?: return HttpResponse.badRequest()
        return HttpResponse.ok(feedback)
    }

    @Get
    fun getEventFeedbacks(
            @PathVariable eventId: Long,
            @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE) page: Int,
            @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE_SIZE) size: Int,
            principal: Principal
    ): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId)
                ?: return HttpResponse.badRequest(createMessageResponse("There is no such event"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest(createMessageResponse("Event or one of organizers groups is blocked"))
        }
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (event.endTime > LocalDateTime.now()) {
            return HttpResponse.badRequest(createMessageResponse("Event is not ended"))
        }
        return HttpResponse.ok(eventFeedbackService.findByEventId(eventId, page, size))
    }

}