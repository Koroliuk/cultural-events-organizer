package com.koroliuk.repository

import com.koroliuk.model.Ticket
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface TicketRepository : CrudRepository<Ticket, Long> {

    fun findByUserId(userId: Long): MutableIterable<Ticket>

}
