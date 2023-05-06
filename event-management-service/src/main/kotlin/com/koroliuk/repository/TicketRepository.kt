package com.koroliuk.repository

import com.koroliuk.model.Event
import com.koroliuk.model.Ticket
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface TicketRepository : CrudRepository<Ticket, Long> {

    fun findByUserId(userId: Long): MutableIterable<Ticket>

    fun findByEvent(event: Event): List<Ticket>

}
