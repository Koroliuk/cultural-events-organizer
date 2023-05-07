package com.ems.service

import com.ems.model.EventCategory

interface EventCategoryService {

    fun create(eventCategory: EventCategory)

    fun update(eventCategory: EventCategory)

    fun existsById(id: Long): Boolean

    fun findByName(name: String): EventCategory?

    fun deleteById(id: Long)
}
