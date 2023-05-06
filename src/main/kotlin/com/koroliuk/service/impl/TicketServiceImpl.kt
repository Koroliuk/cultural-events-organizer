package com.koroliuk.service.impl

import com.koroliuk.model.Event
import com.koroliuk.model.Ticket
import com.koroliuk.model.User
import com.koroliuk.repository.TicketRepository
import com.koroliuk.service.TicketService
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

    override fun deleteById(id: Long) {
        ticketRepository.deleteById(id)
    }

}
