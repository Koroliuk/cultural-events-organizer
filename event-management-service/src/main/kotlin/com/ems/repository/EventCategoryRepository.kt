package com.ems.repository

import com.ems.model.EventCategory
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface EventCategoryRepository : CrudRepository<EventCategory, Long> {

    fun findByName(name: String): EventCategory?

}
