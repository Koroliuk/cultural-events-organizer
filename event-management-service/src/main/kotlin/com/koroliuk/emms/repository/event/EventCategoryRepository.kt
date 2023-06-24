package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.EventCategory
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository

@Repository
interface EventCategoryRepository : CrudRepository<EventCategory, Long> {

    fun findByName(name: String): EventCategory?

    fun findAll(pageable: Pageable): Page<EventCategory>

}
