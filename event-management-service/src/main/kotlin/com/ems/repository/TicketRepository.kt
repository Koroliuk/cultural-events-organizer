package com.ems.repository

import com.ems.model.Event
import com.ems.model.Ticket
import com.ems.model.TicketStatus
import com.ems.model.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface TicketRepository : CrudRepository<Ticket, Long> {

    fun findByUserId(userId: Long): MutableIterable<Ticket>

    fun findByEvent(event: Event): List<Ticket>

    fun findByEventAndUser(event: Event, user: User): Ticket?

    fun countByStatusAndEventId(status: TicketStatus, eventId: Long): Long

}
