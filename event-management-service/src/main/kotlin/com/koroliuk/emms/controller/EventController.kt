package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.dto.EventDto
import com.koroliuk.emms.controller.request.PurchaseRequest
import com.koroliuk.emms.exception.EventNotFoundException
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventType
import com.koroliuk.emms.model.user.*
import com.koroliuk.emms.service.*
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import com.koroliuk.emms.utils.ControllerUtils.getCurrentUser
import com.koroliuk.emms.utils.ControllerUtils.isManagerOfEvent
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid


@Controller("/api/events")
class EventController(@Inject private val eventService: EventService, @Inject private val userService: UserService, @Inject private val discountCodeService: DiscountCodeService, @Inject private val attendanceEntryService: AttendanceEntryService, @Inject private val priceCategoryService: PriceCategoryService, @Inject private val seatService: SeatService, @Inject private val groupService: GroupService, @Inject private val complaintService: ComplaintService) {

    @Get("/{id}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun findEventById(@PathVariable id: Long): HttpResponse<Any> {
        return try {
            val response = eventService.getEventInfo(id)
            HttpResponse.ok(response)
        } catch (e: EventNotFoundException) {
            HttpResponse.notFound(createMessageResponse(e.message))
        }
    }

    @Get("/private/{id}")
    @Secured(USER)
    fun findEventByIdPrivate(@PathVariable id: Long, @QueryValue invitationCode: String, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        return try {
            val response = eventService.getEventInfPrivate(id, user, invitationCode)
            HttpResponse.ok(response)
        } catch (e: EventNotFoundException) {
            HttpResponse.notFound(createMessageResponse(e.message))
        }
    }

    @Get
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun searchEvents(@QueryValue keywords: List<String>?, @QueryValue location: String?, @QueryValue startTime: LocalDateTime?, @QueryValue endTime: LocalDateTime?, @QueryValue categories: List<String>?, @QueryValue(defaultValue = "0") page: Int, @QueryValue(defaultValue = "10") size: Int, @QueryValue(defaultValue = "name") sort: String, @QueryValue(defaultValue = "asc") sortType: String, @QueryValue eventType: EventType?): HttpResponse<Any> {
        if (sort != "name" && sort != "start_time" && sort != "end_time") {
            return HttpResponse.badRequest()
        }
        return try {
            val events = eventService.searchEvents1(keywords, categories, startTime, endTime, location, eventType, page, size, sort, sortType)
            HttpResponse.ok(events)
        } catch (e: Exception) {
            e.printStackTrace()
            HttpResponse.serverError(e.message)
        }
    }

    @Post
    @Secured(USER)
    fun create(@Body eventDto: EventDto, principal: Principal): HttpResponse<Any> {
        return try {
            val user = getCurrentUser(principal, userService)
            validateUserAndManagerStatusForCreate(eventDto, user)
            validateEventDto(eventDto)
            HttpResponse.ok(eventService.create(eventDto))
        } catch (e: IllegalArgumentException) {
            HttpResponse.badRequest(createMessageResponse(e.message))
        } catch (e: IllegalAccessException) {
            HttpResponse.status<Any>(HttpStatus.FORBIDDEN).body(createMessageResponse(e.message))
        }
    }

    @Put("/{id}")
    @Secured(USER)
    fun update(@PathVariable id: Long, @Body eventDto: EventDto, principal: Principal): HttpResponse<Any> {
        return try {
            val event = eventService.findById(id)
                    ?: return HttpResponse.badRequest(createMessageResponse("No event with such id"))
            val user = getCurrentUser(principal, userService)
            validateUserAndManagerStatusForCurrent(event, user)
            validateUserAndManagerStatusForCreate(eventDto, user)
            validateEventDto(eventDto)
            HttpResponse.ok(eventService.update(event, eventDto))
        } catch (e: IllegalArgumentException) {
            HttpResponse.badRequest(createMessageResponse(e.message))
        } catch (e: IllegalAccessException) {
            HttpResponse.status<Any>(HttpStatus.FORBIDDEN).body(createMessageResponse(e.message))
        }
    }

    private fun validateUserAndManagerStatusForCurrent(event: Event, user: User) {
        if (user.isBlocked) {
            throw IllegalAccessException("User is blocked")
        }
        val eventOrganizers = eventService.findEventOrganizers(event.id!!)
        if (eventOrganizers.isEmpty()) {
            throw IllegalAccessException("User is not a manager of any group")
        }
        if (eventOrganizers.stream().map { eventOrganizer -> eventOrganizer.group }.peek {
                    if (it.isBlocked) {
                        throw IllegalAccessException("Current group is blocked")
                    }
                }.map { it.managers }.flatMap { it.stream() }.map { it.user }.noneMatch { u -> u.username == user.username }) {
            throw IllegalAccessException("User is not a manager of any group")
        }
    }

    @Delete("/{id}")
    @Secured(USER)
    fun deleteById(id: Long, principal: Principal): HttpResponse<Any> {
        val event = eventService.findById(id)
                ?: return HttpResponse.badRequest(createMessageResponse("There is no such event"))
        if (!isManagerOfEvent(id, principal, eventService)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN).body(createMessageResponse("You are not event manager"))
        }
        eventService.delete(event)
        return HttpResponse.noContent()
    }

    @Post("/{eventId}/attendances")
    @Secured(USER)
    fun purchaseTicket(@PathVariable eventId: Long, @Body purchaseRequest: PurchaseRequest, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.badRequest()
        }
        val discountCode = if (purchaseRequest.discountCode != null) {
            val code = discountCodeService.findByCode(purchaseRequest.discountCode)
            if (code != null) {
                if (code.expirationDate.isBefore(LocalDateTime.now())) {
                    return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Discount code is expired"))
                }
                if (code.event.id != event.id) {
                    return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Discount code is not applicable for this event"))
                }
            } else {
                return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Invalid discount code"))
            }
            code
        } else null
        val seat = if (purchaseRequest.seatId != null) {
            seatService.findById(purchaseRequest.seatId)
                    ?: return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Invalid seat id"))
        } else null
        val priceCategory = if (purchaseRequest.priceCategoryId != null) {
            priceCategoryService.findById(purchaseRequest.priceCategoryId)
                    ?: return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Invalid price category id"))
        } else null

        return try {
            attendanceEntryService.purchaseTicket(event, user, seat, priceCategory, discountCode, purchaseRequest.isUnSubscribeFromWaitingList)
            HttpResponse.ok()
        } catch (e: Exception) {
            HttpResponse.badRequest(createMessageResponse(e.message))
        }
    }


    @Post("/private/{eventId}/attendances")
    @Secured(USER)
    fun purchaseTicketForPrivateEvent(@PathVariable eventId: Long, @QueryValue invitationCode: String, @Valid @Body purchaseRequest: PurchaseRequest, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        event.privateEvent ?: return HttpResponse.badRequest()
        if (event.privateEvent!!.invitationCode != invitationCode) {
            return HttpResponse.badRequest()
        }
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.badRequest()
        }
        val discountCode = if (purchaseRequest.discountCode != null) {
            val code = discountCodeService.findByCode(purchaseRequest.discountCode)
            if (code != null) {
                if (code.expirationDate.isBefore(LocalDateTime.now())) {
                    return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Discount code is expired"))
                }
                if (code.event.id != event.id) {
                    return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Discount code is not applicable for this event"))
                }
            } else {
                return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Invalid discount code"))
            }
            code
        } else null
        val seat = if (purchaseRequest.seatId != null) {
            seatService.findById(purchaseRequest.seatId)
                    ?: return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Invalid seat id"))
        } else null
        val priceCategory = if (purchaseRequest.priceCategoryId != null) {
            priceCategoryService.findById(purchaseRequest.priceCategoryId)
                    ?: return HttpResponse.status<Any?>(HttpStatus.BAD_REQUEST).body(createMessageResponse("Invalid price category id"))
        } else null

        return try {
            attendanceEntryService.purchaseTicket(event, user, seat, priceCategory, discountCode, purchaseRequest.isUnSubscribeFromWaitingList)
            HttpResponse.ok()
        } catch (e: Exception) {
            HttpResponse.badRequest(createMessageResponse(e.message))
        }
    }

    @Get("/{eventId}/complaints")
    @Secured(USER)
    fun getEventComplaints(@PathVariable eventId: Long, @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE) page: Int, @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE_SIZE) size: Int, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest(createMessageResponse("Event or one of organizers groups is blocked"))
        }
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        return HttpResponse.ok(complaintService.getEventComplaints(eventId, page, size))
    }

    @Get("/{eventId}/analytics")
    @Secured(USER)
    fun getEventAnalytics(@PathVariable eventId: Long, principal: Principal): HttpResponse<Any> {
        val user = getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest(createMessageResponse("Event or one of organizers groups is blocked"))
        }
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (event.endTime > LocalDateTime.now()) {
            return HttpResponse.badRequest(createMessageResponse("Event is not ended yet"))
        }
        return HttpResponse.ok(eventService.getEventAnalytics(event))
    }

    private fun validateEventDto(eventDto: EventDto) {
        if (eventDto.startTime >= eventDto.endTime) {
            throw IllegalArgumentException("Start date must be earlier than end date")
        }
        if (EventType.ONLINE == eventDto.eventType && eventDto.url == null) {
            throw IllegalArgumentException("Url must be filled for online event")
        }
        if (EventType.OFFLINE == eventDto.eventType && eventDto.location == null) {
            throw IllegalArgumentException("Location must be filled for offline event")
        }
    }

    private fun validateUserAndManagerStatusForCreate(eventDto: EventDto, user: User) {
        if (user.isBlocked) {
            throw IllegalAccessException("User is blocked")
        }
        if (!isManagerOfOneGroupAndGroupIsNotBlocked(eventDto, user)) {
            throw IllegalAccessException("User is not a manager of any group")
        }
    }

    private fun isManagerOfOneGroupAndGroupIsNotBlocked(eventDto: EventDto, user: User): Boolean {
        for (groupId in eventDto.organizersGroups) {
            val group = groupService.findById(groupId) ?: throw IllegalArgumentException("No group with such id")
            if (group.isBlocked) {
                throw IllegalAccessException("One of the organizers groups is blocked")
            }
            if (groupService.getGroupManagers(group).stream().anyMatch { it.username == user.username }) {
                return true
            }
        }
        return false
    }

}
