package com.ems.controller

import com.ems.dto.DiscountCodeRequest
import com.ems.model.DiscountCode
import com.ems.service.DiscountCodeService
import com.ems.service.EventService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject

@Controller("/api/discount-codes")
@Secured(SecurityRule.IS_AUTHENTICATED)
class DiscountCodeController(
    @Inject private val discountCodeService: DiscountCodeService,
    @Inject private val eventService: EventService
) {

    @Get("/{eventId}")
    @Secured("USER")
    fun getEventDiscountCodes(eventId: Long): List<DiscountCode> {
        val event = eventService.findById(eventId)
        return discountCodeService.findByEvent(event)
    }

    @Post
    @Secured("ADMIN")
    fun createDiscountCode(@Body discountCodeRequest: DiscountCodeRequest): HttpResponse<Any> {
        val event = eventService.findById(discountCodeRequest.eventId)
        val discountCode = discountCodeService.createDiscountCode(
            event,
            discountCodeRequest.code,
            discountCodeRequest.discountPercentage
        )
        return HttpResponse.created(discountCode)
    }

    @Delete("/{id}")
    @Secured("ADMIN")
    fun deleteDiscountCode(@PathVariable id: Long): HttpResponse<Any> {
        discountCodeService.deleteDiscountCode(id)
        return HttpResponse.noContent()
    }

}
