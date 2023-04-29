package com.koroliuk.controller

import com.koroliuk.dto.EventDto
import com.koroliuk.dto.PurchaseRequest
import com.koroliuk.model.Event
import com.koroliuk.model.EventType
import com.koroliuk.service.EventService
import com.koroliuk.service.TicketService
import com.koroliuk.service.UserService
import com.koroliuk.utils.MappingUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal

@Controller("/api/events")
@Secured(SecurityRule.IS_AUTHENTICATED)
class EventController(
    @Inject private val eventService: EventService,
    @Inject private val ticketService: TicketService,
    @Inject private val userService: UserService,
) {

    @Post
    @Secured("USER")
    fun create(@Body eventDto: EventDto, principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)
        if (user != null) {
            if (user.blocked) {
                return HttpResponse.status(HttpStatus.FORBIDDEN)
            }
            if (eventDto.startTime > eventDto.endTime) {
                throw IllegalArgumentException("Start date must be earlier than end date")
            }
            if (EventType.ONLINE == eventDto.eventType && eventDto.url == null) {
                throw IllegalArgumentException("Url must be filled for online event")
            }
            if (EventType.OFFLINE == eventDto.eventType && eventDto.location == null) {
                throw IllegalArgumentException("Location must be filled for offline event")
            }
            val event = MappingUtils.convertToEntity(eventDto)
            return HttpResponse.created(eventService.create(event))
        }
        return HttpResponse.badRequest();
    }


    @Put("/{id}")
    @Secured("USER")
    fun update(@PathVariable id: Long, @Body eventDto: EventDto): Event {
        if (!eventService.existById(id)) {
            throw IllegalArgumentException("No event with a such id")
        }
        val event = MappingUtils.convertToEntity(eventDto)
        event.id = id
        return eventService.update(event)
    }

    @Get("/{id}")
    @Secured("USER")
    fun findById(@PathVariable id: Long): Event {
        return eventService.findById(id)
    }

    @Get
    @Secured("USER")
    fun findAll(): MutableIterable<Event> = eventService.findAll()

    @Get("/search{?keywords}")
    @Secured("USER")
    fun searchEvents(keywords: List<String>?): MutableIterable<Event> {
        return if (keywords != null) {
            eventService.searchEventsByKeywords(keywords)
        } else {
            eventService.findAll()
        }
    }

    @Delete("/{id}")
    @Secured("USER","ADMIN")
    fun deleteById(id: Long): HttpResponse<Any> {
        //todo: delete tickets also
        eventService.deleteById(id)
        return HttpResponse.status(HttpStatus.NO_CONTENT)
    }

    @Post("{eventId}/tickets")
    @Secured("USER")
    fun purchaseTicket(@PathVariable eventId: Long, @Body purchaseRequest: PurchaseRequest,
                       principal: Principal): HttpResponse<Any> {
        if (!eventService.existById(eventId)) {
            throw java.lang.IllegalArgumentException("There is no such event")
        }
        val amount = purchaseRequest.amount
        val event = eventService.findById(eventId)
        val user = userService.findByUsername(principal.name)
        if (user != null) {
            if (user.blocked) {
                return HttpResponse.status(HttpStatus.FORBIDDEN)
            }
            ticketService.purchaseTickets(event, user, amount)
            return HttpResponse.ok()
        }
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}
