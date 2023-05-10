package com.ems.controller

import com.ems.service.EventService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.security.Principal


@Controller("/api/user")
@Secured("USER")
class UserController(
    @Inject private val eventService: EventService
) {

    @Get("/attended")
    fun getAttendedEvents(principal: Principal): HttpResponse<Any> {
        return HttpResponse.ok(eventService.getAttendedEvent(principal.name))
    }

}
