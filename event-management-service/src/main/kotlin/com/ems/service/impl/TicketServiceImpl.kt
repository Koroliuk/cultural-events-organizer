package com.ems.service.impl

import com.ems.model.*
import com.ems.repository.TicketRepository
import com.ems.service.NotificationService
import com.ems.service.TicketService
import com.ems.service.WaitListService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.Instant
import java.time.LocalDateTime
import java.util.stream.Collectors


@Singleton
class TicketServiceImpl(
    @Inject private val ticketRepository: TicketRepository,
    @Inject private val waitListService: WaitListService,
    @Inject private val notificationService: NotificationService
) : TicketService {
    override fun update(ticket: Ticket) {
        ticketRepository.update(ticket)
    }

    override fun purchaseTickets(event: Event, user: User, amount: Long, discountCode: DiscountCode?, isUnSubscribeFromWaitingList: Boolean) {
        if (event.endTime < LocalDateTime.now()) {
            throw IllegalArgumentException("Event already ended")
        }
        if (event.isTicketsLimited && ticketRepository.findByEvent(event).size + amount > event.maxTickets!!) {
            throw IllegalArgumentException("Not enough tickets")
        }
        for (i in 1..amount) {
            val price = if (discountCode != null) {
                calculateDiscountedPrice(event.price, discountCode.discountPercentage)
            } else {
                event.price
            }
            val ticket = Ticket(event, user, Instant.now(), price, TicketStatus.ACTIVE)
            ticketRepository.save(ticket)
        }
        if (isUnSubscribeFromWaitingList) {
            waitListService.deleteByEventAndUser(event, user)
        }
    }

    private fun calculateDiscountedPrice(originalPrice: Double, discountPercentage: Float): Double {
        return originalPrice * (1 - discountPercentage / 100)
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

    override fun findByEvent(event: Event): List<Ticket> {
        return ticketRepository.findByEvent(event)
    }

    override fun countByStatusAndEventId(status: TicketStatus, eventId: Long): Long {
        return ticketRepository.countByStatusAndEventId(status, eventId)
    }

    override fun cancelById(id: Long) {
        val optionalTicket = ticketRepository.findById(id)
        if (optionalTicket.isEmpty) {
            throw IllegalArgumentException("No ticket with such id")
        }
        val ticket = optionalTicket.get()
        ticket.status = TicketStatus.CANCELED
        val event = ticket.event
        if (event.isTicketsLimited) {
            waitListService.findByEvent(event).stream()
                .map { w -> w.user }
                .forEach { user -> notificationService.addNotificationForUser(user.username, "There is new tickets for this event", NotificationType.EVENT_NEW_TICKETS, event) }
        }
        ticketRepository.update(ticket)
    }

}
