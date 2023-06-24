package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.GetEventCategoriesResponse
import com.koroliuk.emms.model.event.EventCategory

interface EventCategoryService {

    fun create(eventCategory: EventCategory)

    fun update(eventCategory: EventCategory)

    fun existsById(id: Long): Boolean

    fun findByName(name: String): EventCategory?

    fun deleteById(id: Long)

    fun findAll(pageNumber: Int, size: Int): GetEventCategoriesResponse
}
