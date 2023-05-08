package com.ems.service

import com.ems.model.Event
import com.ems.model.Ticket
import com.ems.model.TicketStatus
import com.ems.model.User

interface TicketService {

    fun purchaseTickets(event: Event, user: User, amount: Long)

    fun findPurchasedTicketsByUserId(userId: Long) : MutableIterable<Ticket>

    fun findUsersByEvent(event: Event): Set<User>

    fun findByEventAndUser(event: Event, user: User): Ticket?

    fun countByStatusAndEventId(status: TicketStatus, eventId: Long): Long

    fun cancelById(id: Long)

}
