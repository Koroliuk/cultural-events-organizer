package com.koroliuk.service

import com.koroliuk.model.Event
import com.koroliuk.model.Ticket
import com.koroliuk.model.User

interface TicketService {

    fun purchaseTickets(event: Event, user: User, amount: Long)

    fun findPurchasedTicketsByUserId(userId: Long) : MutableIterable<Ticket>

    fun deleteById(id: Long)

}
