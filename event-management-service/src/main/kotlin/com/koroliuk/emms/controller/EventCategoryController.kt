package com.koroliuk.emms.controller

import com.koroliuk.emms.utils.ControllerUtils
import com.koroliuk.emms.service.EventCategoryService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject

@Controller("/api/categories")
@Secured(SecurityRule.IS_ANONYMOUS)
class EventCategoryController(
    @Inject private val eventCategoryService: EventCategoryService,
) {

    @Get
    fun getEventCategories(
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE) page: Int,
        @QueryValue(defaultValue = ControllerUtils.DEFAULT_PAGE_SIZE) size: Int
    ): HttpResponse<Any> {
        return HttpResponse.ok(eventCategoryService.findAll(page, size))
    }

}
