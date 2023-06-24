package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventOrganizer
import com.koroliuk.emms.model.group.Group
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository

@Repository
interface EventOrganizerRepository: CrudRepository<EventOrganizer, Long> {
    fun findByEvent(event: Event): List<EventOrganizer>

    fun findByGroupAndEventBlocked(group: Group, blocked: Boolean, pageable: Pageable): Page<EventOrganizer>

    fun findAllByEventId(eventId: Long): List<EventOrganizer>

    fun deleteByEventAndGroup(event: Event, group: Group)

    fun deleteByEventId(event: Long)

    fun deletedByGroupId(id: Long)
}
