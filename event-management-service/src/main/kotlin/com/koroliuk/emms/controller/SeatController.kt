package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.request.SeatDto
import com.koroliuk.emms.controller.request.SeatUpdateDto
import com.koroliuk.emms.model.attendance.PriceCategory
import com.koroliuk.emms.model.attendance.Seat
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.PriceCategoryService
import com.koroliuk.emms.service.SeatService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal
import javax.validation.Valid


@Controller("/api/events/{eventId}/seats")
class SeatController(
    @Inject private val seatService: SeatService,
    @Inject private val eventService: EventService,
    @Inject private val priceCategoryService: PriceCategoryService,
    @Inject private val userService: UserService
) {

    @Get
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getAllSeatByEventId(
        eventId: Long,
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE_SIZE) size: Int
    ): HttpResponse<Any> {
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        return HttpResponse.ok(seatService.getAllByEventId(eventId, Pageable.from(page, size)))
    }

    @Get("/{id}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getByIdSeat(eventId: Long, id: Long): HttpResponse<Any> {
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        val seat = seatService.getById(id) ?: return HttpResponse.badRequest()
        return HttpResponse.ok(seat)
    }

    @Delete("/{id}")
    @Secured(USER)
    fun deleteSeat(eventId: Long, id: Long, principal: Principal): HttpResponse<Any> {
        eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (!ControllerUtils.isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (!seatService.existsById(id)) {
            return HttpResponse.notFound(createMessageResponse("No seat with such id"))
        }
        seatService.delete(id)
        return HttpResponse.noContent()
    }

    @Post
    @Secured(USER)
    fun create(@PathVariable eventId: Long, @Body @Valid seatDto: SeatDto, principal: Principal): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        if (!ControllerUtils.isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not manager of this event"))
        }
        val priceCategory = priceCategoryService.findById(seatDto.priceCategoryId) ?: return HttpResponse.badRequest()
        if (priceCategory.event.id != event.id) {
            return HttpResponse.badRequest<Any?>()
                .body(createMessageResponse("Please check input data regarding event and priceCategory"))
        }
        return HttpResponse.created(seatService.create(seatDto, event, priceCategory))
    }

    @Put("/{id}")
    @Secured(USER)
    fun update(
        @PathVariable eventId: Long,
        id: Long,
        @Body @Valid seatDto: SeatUpdateDto,
        principal: Principal
    ): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        if (!ControllerUtils.isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not manager of this event"))
        }
        val seat = seatService.findById(id) ?: return HttpResponse.badRequest()
        if (seat.event.id != event.id) {
            return HttpResponse.badRequest()
        }
        var priceCategory: PriceCategory? = null
        if (seatDto.priceCategoryId != null) {
            priceCategory = priceCategoryService.findById(seatDto.priceCategoryId) ?: return HttpResponse.badRequest()
            if (priceCategory.event.id != event.id) {
                return HttpResponse.badRequest()
            }
        }
        return HttpResponse.ok(seatService.update(seat, seatDto, priceCategory))
    }


}
