package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.request.EmailSubscribtionRequest
import com.koroliuk.emms.controller.request.SaveRequest
import com.koroliuk.emms.controller.response.*
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.GroupService
import com.koroliuk.emms.service.AttendanceEntryService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE_SIZE
import com.koroliuk.emms.utils.ControllerUtils.getCurrentUser
import com.koroliuk.emms.utils.ControllerUtils.isManagerOfEvent
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.security.Principal
import java.time.LocalDateTime
import javax.validation.Valid


@Controller("/api/user")
@Secured(USER)
class UserController(
    @Inject private val eventService: EventService,
    @Inject private val userService: UserService,
    @Inject private val groupService: GroupService,
    @Inject private val attendanceEntryService: AttendanceEntryService
) {

    @Get("/complaints")
    fun getUsersComplaints(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): HttpResponse<GetComplaintsResponse> {
        val user = getCurrentUser(principal, userService)
        val complaintInfosPage = userService.getUserComplaints(page, size, user)
        return HttpResponse.ok(complaintInfosPage)
    }

    @Post("/saved-events")
    fun saveEvent(@Valid @Body request: SaveRequest, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)!!
        val eventId = request.objectId
        val event = eventService.findById(eventId) ?: return HttpResponse.notFound()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (userService.isEventSavedByUser(eventId, user)) {
            return HttpResponse.ok()
        }
        val saveEvent = userService.saveEvent(eventId, user)
        val result = SaveEventResponse(
            id = saveEvent.id!!,
            eventId = saveEvent.event.id!!
        )
        return HttpResponse.ok(result)
    }

    @Get("/saved-events/{id}")
    fun getSavedEventById(@PathVariable id: Long, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)!!
        val savedEvent = userService.findSavedEventById(id) ?: return HttpResponse.notFound()
        if (savedEvent.user.username != user.username) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val result = SaveEventResponse(
            id = savedEvent.id!!,
            eventId = savedEvent.event.id!!
        )
        return HttpResponse.ok(result)
    }

    @Get("/saved-events")
    fun getAllSavedEvents(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): GetSavedEventsResponse {
        val user = getCurrentUser(principal, userService)
        return userService.getAllSavedEventsByUser(page, size, user)
    }

    @Delete("/saved-events/{id}")
    fun deleteSavedEventById(@PathVariable id: Long): HttpResponse<Any> {
        userService.deleteSavedEventById(id)
        return HttpResponse.noContent()
    }

    @Post("/saved-groups/")
    fun saveGroup(@Valid @Body request: SaveRequest, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)!!
        val groupId = request.objectId
        val group = groupService.findById(groupId) ?: return HttpResponse.notFound()
        if (group.isBlocked) {
            return HttpResponse.badRequest()
        }
        if (userService.isGroupSavedByUser(groupId, user)) {
            return HttpResponse.ok()
        }
        val saveEvent = groupService.saveGroup(groupId, user)
        val result = SaveGroupResponse(
            id = saveEvent.id!!,
            groupId = saveEvent.group.id!!
        )
        return HttpResponse.ok(result)
    }

    @Get("/saved-groups/{id}")
    fun getSavedGroupById(@PathVariable id: Long, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val savedEvent = userService.findSavedGroupById(id) ?: return HttpResponse.notFound()
        if (savedEvent.user.username != user.username) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val result = SaveGroupResponse(
            id = savedEvent.id!!,
            groupId = savedEvent.group.id!!
        )
        return HttpResponse.ok(result)
    }

    @Get("/saved-groups")
    fun getAllSavedGroups(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): GetSavedGroupsResponse {
        val user = getCurrentUser(principal, userService)
        return userService.getAllSavedGroupsByUser(page, size, user)
    }

    @Delete("/saved-groups/{id}")
    fun deleteSavedGroupById(@PathVariable id: Long): HttpResponse<Any> {
        userService.deleteSavedGroupById(id)
        return HttpResponse.noContent()
    }

    @Put("/email-subscription")
    fun subscribeForEmailNotifications(@Body request: EmailSubscribtionRequest, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        user.isSubscribedFor = request.isSubscribed
        userService.update(user)
        return HttpResponse.ok()
    }

    @Post("/groups/{groupId}/group-subscriptions")
    fun subscribe(groupId: Long, principal: Principal): HttpResponse<Any> {
        val group = groupService.findById(groupId) ?: return HttpResponse.notFound()
        if (group.isBlocked) {
            return HttpResponse.badRequest()
        }
        val user = userService.findByUsername(principal.name)!!
        if (userService.isSubcribetToThisGroup(groupId, user)) {
            return HttpResponse.ok()
        }
        return HttpResponse.ok(groupService.subcribeToGroup(groupId, user))
    }

    @Delete("/groups/{groupId}/group-subscriptions")
    fun unsubscribe(@QueryValue groupId: Long, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)!!
        userService.deleteSubscriptionByGroupIdAndUser(groupId, user)
        return HttpResponse.noContent()
    }

    @Get("/groups/group-subscriptions")
    fun getUserSubscriptions(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): GetGroupSubscriptionsResponse {
        val user = userService.findByUsername(principal.name)!!
        return userService.getUserSubscriptions(page, size, user)
    }

    @Get("/volunteer-applications")
    fun getCreatedVolunteerApplications(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): GetVolunteerApplicationsResponse {
        val user = getCurrentUser(principal, userService)
        return userService.getUserVolunteerApplications(user, page, size)
    }

    @Get("/groups")
    fun getOwningGroups(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        return HttpResponse.ok(groupService.getGroupByUser(user, page, size))
    }

    @Get("/attendances")
    fun getPurchasedTickets(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        principal: Principal
    ): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val tickets = attendanceEntryService.findPurchasedTicketsByUserIdPaginated(user.id!!, page, size)
        return HttpResponse.ok(tickets)
    }

    @Delete("/attendances/{id}")
    fun cancelById(id: Long, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        val attendance = attendanceEntryService.findById(id) ?: return HttpResponse.badRequest()
        if (user.username != attendance.user.username) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        attendanceEntryService.cancelById(id)
        return HttpResponse.status(HttpStatus.NO_CONTENT)
    }

    @Post("/{eventId}/wait")
    fun addToWaitingList(eventId: Long, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (event.endTime <= LocalDateTime.now()) {
            return HttpResponse.badRequest()
        }
        eventService.waitForEventTickets(eventId, user.username)
        return HttpResponse.ok()
    }

    @Post("/{eventId}/unwait/")
    fun removeFromWaitingList(eventId: Long, principal: Principal): HttpResponse<Any> {
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        eventService.unWaitForEventTickets(eventId, principal.name)
        return HttpResponse.ok()
    }

}
