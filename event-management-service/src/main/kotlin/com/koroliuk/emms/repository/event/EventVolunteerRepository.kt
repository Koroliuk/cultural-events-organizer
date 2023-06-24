package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.EventVolunteers
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface EventVolunteerRepository : CrudRepository<EventVolunteers, Long> {

    fun findByEventId(id: Long): EventVolunteers?

    fun deleteByEventId(id: Long)

}