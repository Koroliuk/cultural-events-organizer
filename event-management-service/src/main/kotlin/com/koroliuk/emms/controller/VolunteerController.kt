package com.koroliuk.emms.controller

import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.model.user.VolunteerApplicationStatus
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.service.VolunteerApplicationService
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import com.koroliuk.emms.utils.ControllerUtils.getCurrentUser
import com.koroliuk.emms.utils.ControllerUtils.isManagerOfEvent
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.security.Principal
import java.time.LocalDateTime


@Controller("/api/events/{eventId}/volunteer-applications")
@Secured(USER)
class VolunteerController(
    @Inject private val userService: UserService,
    @Inject private val eventService: EventService,
    @Inject private val volunteerApplicationService: VolunteerApplicationService
) {

    @Post
    fun createVolunteerApplication(
        @PathVariable eventId: Long,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (!eventService.isEventAskingForVolunteers(event) || event.endTime <= LocalDateTime.now()) {
            return HttpResponse.badRequest(createMessageResponse("Event is not requering volunteers"))
        }
        return HttpResponse.ok(volunteerApplicationService.create(event, user))
    }

    @Delete("/{id}")
    fun deleteVolunteerApplication(
        @PathVariable eventId: Long,
        principal: Principal, @PathVariable id: Long
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        val volunteerApplication =
            volunteerApplicationService.findById(id) ?: return HttpResponse.badRequest(createMessageResponse("There is no SUCH application"))
        if (volunteerApplication.user.username != user.username) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        volunteerApplicationService.deleteById(id)
        return HttpResponse.noContent()
    }

    @Get
    fun getVolunteerApplicationsByEventId(
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE_SIZE) size: Int,
        @PathVariable eventId: Long, principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (!isManagerOfEvent(event.id!!, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        return HttpResponse.ok(volunteerApplicationService.findByEvent(event, page, size))
    }

    @Put("/{id}/approve")
    fun approveVolunteerApplication(
        @PathVariable eventId: Long,
        @PathVariable id: Long, principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (!isManagerOfEvent(event.id!!, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val volunteerApplication =
                volunteerApplicationService.findById(id)
                        ?: return HttpResponse.badRequest("There is no SUCH application")
        if (volunteerApplication.status == VolunteerApplicationStatus.PENDING) {
            if (volunteerApplication.event.id == event.id) {
                volunteerApplication.status = VolunteerApplicationStatus.APPROVED
                volunteerApplicationService.update(volunteerApplication)
                return HttpResponse.ok(createMessageResponse("Application approved"))
            }
        }
        return HttpResponse.badRequest()
    }

    @Put("/{id}/reject")
    fun rejectVolunteerApplication(
        @PathVariable eventId: Long, @PathVariable id: Long, principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (!isManagerOfEvent(event.id!!, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val volunteerApplication =
            volunteerApplicationService.findById(id) ?: return HttpResponse.badRequest("There is no SUCH application")
        if (volunteerApplication.status == VolunteerApplicationStatus.PENDING) {
            if (volunteerApplication.event.id == event.id) {
                volunteerApplication.status = VolunteerApplicationStatus.REJECTED
                volunteerApplicationService.update(volunteerApplication)
                return HttpResponse.ok(createMessageResponse("Application rejected"))
            }
        }
        return HttpResponse.badRequest()
    }

}