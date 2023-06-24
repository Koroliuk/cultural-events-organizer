package com.koroliuk.emms.controller

import com.koroliuk.emms.utils.ControllerUtils.isManagerOfEvent
import com.koroliuk.emms.controller.request.DiscountCodeRequest
import com.koroliuk.emms.model.attendance.DiscountType
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.DiscountCodeService
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE_SIZE
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.security.Principal
import java.time.LocalDateTime

@Controller("/api/events/{eventId}/discount-codes")
@Secured(USER)
class DiscountCodeController(
    @Inject private val discountCodeService: DiscountCodeService,
    @Inject private val eventService: EventService,
    @Inject private val userService: UserService
) {

    @Get
    fun getEventDiscountCodes(
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int,
        @PathVariable eventId: Long,
        principal: Principal
    ): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
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
        return HttpResponse.ok(discountCodeService.findByEvent(event, page, size))
    }

    @Get("/{id}")
    fun getDiscountCodeById(
        @PathVariable id: Long,
        @PathVariable eventId: Long,
        principal: Principal
    ): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        val discountCodeInfo = discountCodeService.getDiscountCodeInfo(id) ?: return HttpResponse.notFound(
            createMessageResponse("No code with such id")
        )
        if (!isManagerOfEvent(discountCodeInfo.eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        return HttpResponse.ok(discountCodeInfo)
    }

    @Post
    fun createDiscountCode(@PathVariable eventId: Long, @Body request: DiscountCodeRequest, principal: Principal): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest(createMessageResponse("There is no event with such id"))
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.badRequest()
        }
        if (!isManagerOfEvent(event.id!!, principal, eventService)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not manager of this event"))
        }
        if (discountCodeService.findByCode(request.code) != null) {
            return HttpResponse.status<Any?>(HttpStatus.CONFLICT)
                .body(createMessageResponse("Code must be unique"))
        }
        if (request.value < 0) {
            return HttpResponse.badRequest(createMessageResponse("Value must be positive"))
        }
        if (request.type == DiscountType.PERCENTAGE && (request.value <= 0 || request.value > 100)) {
            return HttpResponse.badRequest(createMessageResponse("For this type, the value must be in range (0, 100]"))
        }
        if (request.expiredDate <= LocalDateTime.now()) {
            return HttpResponse.badRequest(createMessageResponse("Wrong expired date"))
        }
        val discountCode = discountCodeService.createDiscountCode(
            event,
            request.code,
            request.type,
            request.value,
            request.expiredDate
        )
        return HttpResponse.created(discountCode)
    }

    @Delete("/{id}")
    fun deleteDiscountCode(
        @PathVariable id: Long,
        @PathVariable eventId: Long,
        principal: Principal
    ): HttpResponse<Any> {
        eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val discountCode = discountCodeService.findById(id)
            ?: return HttpResponse.notFound(createMessageResponse("No code with such id"))
        discountCodeService.deleteDiscountCode(discountCode)
        return HttpResponse.noContent()
    }

}
