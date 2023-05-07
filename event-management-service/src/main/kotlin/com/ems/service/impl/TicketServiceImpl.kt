package com.ems.service.impl

import com.ems.model.Event
import com.ems.model.Ticket
import com.ems.model.User
import com.ems.repository.TicketRepository
import com.ems.service.TicketService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.Instant
import java.time.LocalDateTime
import java.util.stream.Collectors


@Singleton
class TicketServiceImpl(
    @Inject private val ticketRepository: TicketRepository
) : TicketService {

    override fun purchaseTickets(event: Event, user: User, amount: Long) {
        if (event.endTime < LocalDateTime.now()) {
            throw IllegalArgumentException("Event already ended")
        }
        for (i in 1..amount) {
            val ticket = Ticket(event, user, Instant.now())
            ticketRepository.save(ticket)
        }
    }

    override fun findPurchasedTicketsByUserId(userId: Long): MutableIterable<Ticket> {
        return ticketRepository.findByUserId(userId)
    }

    override fun findUsersByEvent(event: Event): Set<User> {
        return ticketRepository.findByEvent(event).stream()
            .map { ticket -> ticket.user }
            .collect(Collectors.toSet())
    }

    override fun findByEventAndUser(event: Event, user: User): Ticket? {
        return ticketRepository.findByEventAndUser(event, user)
    }

    override fun deleteById(id: Long) {
        ticketRepository.deleteById(id)
    }

}
