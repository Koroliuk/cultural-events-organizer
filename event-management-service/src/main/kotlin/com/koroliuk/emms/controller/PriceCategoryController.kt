package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.request.PriceCategoryDto
import com.koroliuk.emms.controller.request.PriceCategoryUpdateDto
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.PriceCategoryService
import com.koroliuk.emms.service.UserService
import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE
import com.koroliuk.emms.utils.ControllerUtils.DEFAULT_PAGE_SIZE
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import com.koroliuk.emms.utils.ControllerUtils.isManagerOfEvent
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal
import javax.validation.Valid


@Controller("/api/events/{eventId}/price-categories")
class PriceCategoryController(
    @Inject private val priceCategoryService: PriceCategoryService,
    @Inject private val eventService: EventService,
    @Inject private val userService: UserService
) {

    @Get
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getAllByEventId(
        eventId: Long,
        @QueryValue(defaultValue = DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = DEFAULT_PAGE_SIZE) size: Int
    ): HttpResponse<Any> {
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        return HttpResponse.ok(priceCategoryService.getAllByEvent(event, Pageable.from(page, size)))
    }

    @Get("/{id}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun getById(eventId: Long, id: Long): HttpResponse<Any> {
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        val priceCategory = priceCategoryService.getById(id) ?: return HttpResponse.notFound()
        return HttpResponse.ok(priceCategory)
    }

    @Delete("/{id}")
    @Secured(USER)
    fun delete(@PathVariable eventId: Long, @PathVariable id: Long, principal: Principal): HttpResponse<Any> {
        eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        if (!priceCategoryService.existsById(id)) {
            return HttpResponse.notFound(createMessageResponse("No price category with such id"))
        }
        priceCategoryService.delete(id)
        return HttpResponse.noContent()
    }

    @Post
    @Secured(USER)
    fun create(@PathVariable eventId: Long, @Body @Valid priceCategoryDTO: PriceCategoryDto, principal: Principal): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not manager of this event"))
        }
        return HttpResponse.created(priceCategoryService.create(event, priceCategoryDTO))
    }

    @Put("/{id}")
    @Secured(USER)
    fun update(@PathVariable eventId: Long, id: Long, @Body @Valid priceCategoryDto: PriceCategoryUpdateDto, principal: Principal): HttpResponse<Any> {
        val user = ControllerUtils.getCurrentUser(principal, userService)
        if (user.isBlocked) {
            return HttpResponse.status(HttpStatus.FORBIDDEN)
        }
        val event = eventService.findById(eventId) ?: return HttpResponse.badRequest()
        if (eventService.isEventOrEventGroupBlocked(event)) {
            return HttpResponse.status<Any?>(HttpStatus.FORBIDDEN)
                    .body(createMessageResponse("This event is blocked or one of group that organizes event"))
        }
        if (!isManagerOfEvent(eventId, principal, eventService)) {
            return HttpResponse.status<Any>(HttpStatus.FORBIDDEN)
                .body(createMessageResponse("You are not manager of this event"))
        }
        val priceCategory = priceCategoryService.findById(id) ?: return HttpResponse.notFound()
        if (priceCategory.event.id != event.id) {
            return HttpResponse.badRequest()
        }
        return HttpResponse.ok(priceCategoryService.update(priceCategory, priceCategoryDto))
    }

}