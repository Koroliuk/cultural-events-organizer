package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.EventMedia
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository

@Repository
interface EventMediaRepository : JpaRepository<EventMedia, Long> {

    fun findAllByEventId(id: Long): List<EventMedia>

    fun findAllByEventId(id: Long, pageable: Pageable): Page<EventMedia>

}
