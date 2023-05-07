package com.ems.controller

import com.ems.dto.EventCategoryDto
import com.ems.service.EventCategoryService
import com.ems.service.UserService
import com.ems.utils.MappingUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.lang.IllegalArgumentException
import java.security.Principal

@Controller("/api/categories")
@Secured("USER")
class EventCategoryController(
        @Inject private val eventCategoryService: EventCategoryService,
        @Inject private val userService: UserService
) {

    @Post
    fun create(@Body eventCategoryDto: EventCategoryDto, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)
        if (user != null) {
            if (user.blocked) {
                return HttpResponse.status(HttpStatus.FORBIDDEN)
            }
            val category = MappingUtils.convertToEntity(eventCategoryDto)
            eventCategoryService.create(category)
            return HttpResponse.ok()
        }
        return HttpResponse.badRequest()
    }

    @Put("/{id}")
    fun update(@PathVariable id: Long, @Body eventCategoryDto: EventCategoryDto): HttpResponse<Any> {
        if (!eventCategoryService.existsById(id)) {
            throw IllegalArgumentException("Incorrect Id")
        }
        val category = MappingUtils.convertToEntity(eventCategoryDto)
        category.id = id
        eventCategoryService.update(category)
        return HttpResponse.ok()
    }

    @Delete
    fun delete(id: Long) : HttpResponse<Any> {
        eventCategoryService.deleteById(id)
        return HttpResponse.status(HttpStatus.NO_CONTENT)
    }

}
