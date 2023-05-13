package com.ems.service

import com.ems.model.*

interface TicketService {

    fun update(ticket: Ticket)

    fun purchaseTickets(event: Event, user: User, amount: Long, discountCode: DiscountCode?, isUnSubscribeFromWaitingList: Boolean)

    fun findPurchasedTicketsByUserId(userId: Long) : MutableIterable<Ticket>

    fun findUsersByEvent(event: Event): Set<User>

    fun findByEventAndUser(event: Event, user: User): Ticket?

    fun findByEvent(event: Event): List<Ticket>

    fun countByStatusAndEventId(status: TicketStatus, eventId: Long): Long

    fun cancelById(id: Long)

}
