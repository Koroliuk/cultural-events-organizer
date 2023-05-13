package com.ems.controller

import com.ems.service.EventCategoryService
import com.ems.service.EventService
import com.ems.service.UserService
import io.micronaut.core.convert.format.Format
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import java.security.Principal
import java.time.LocalDateTime

@Controller("/api/analytics")
@Secured("USER")
class EventAnalyticsController(
    @Inject private val eventService: EventService
) {

    @Get("/{eventId}")
    fun getEventAnalytics(eventId: Long, principal: Principal): HttpResponse<Any> {
        val event = eventService.findById(eventId)
        if (event.creators.stream()
                .noneMatch { u -> principal.name == u.username }
        ) {
            return HttpResponse.notAllowed()
        }
        if (event.endTime > LocalDateTime.now()) {
            return HttpResponse.badRequest("Event is not ended yet")
        }
        return HttpResponse.ok(eventService.getEventAnalytics(event))
    }

    @Get
    fun getEventsAnalytics(
        @QueryValue @Format("yyyy-MM-dd'T'HH:mm:ss'Z'") dateFrom: LocalDateTime?,
        @QueryValue @Format("yyyy-MM-dd'T'HH:mm:ss'Z'") dateTo: LocalDateTime?
    ): HttpResponse<Any> {
        return HttpResponse.ok(eventService.getEventsAnalytics(dateFrom, dateTo))
    }

}
