package com.koroliuk.controller

import com.koroliuk.service.TicketService
import com.koroliuk.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.security.Principal

@Controller("/api/tickets")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TicketController (
    @Inject private val userService: UserService,
    @Inject private val ticketService: TicketService
) {

    @Get
    @Secured("USER")
    fun getPurchasedTickets(principal: Principal): HttpResponse<Any> {
        val user = userService.findByUsername(principal.name)
        if (user != null) {
            val tickets = ticketService.findPurchasedTicketsByUserId(user.id!!)
            return HttpResponse.ok(tickets)
        }
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Delete("/{id}")
    @Secured("USER")
    fun cancelById(id: Long): HttpResponse<Any> {
        ticketService.deleteById(id)
        return HttpResponse.status(HttpStatus.NO_CONTENT)
    }

}
