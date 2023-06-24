package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.GetEventCategoriesResponse
import com.koroliuk.emms.model.event.EventCategory
import com.koroliuk.emms.repository.event.EventCategoryRepository
import com.koroliuk.emms.service.EventCategoryService
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class EventCategoryServiceImpl(
        @Inject private val eventCategoryRepository: EventCategoryRepository
) : EventCategoryService {

    override fun create(eventCategory: EventCategory) {
        eventCategoryRepository.save(eventCategory)
    }

    override fun update(eventCategory: EventCategory) {
        eventCategoryRepository.update(eventCategory)
    }

    override fun existsById(id: Long): Boolean {
        return eventCategoryRepository.existsById(id)
    }

    override fun findByName(name: String): EventCategory? {
        return eventCategoryRepository.findByName(name)
    }

    override fun deleteById(id: Long) {
        eventCategoryRepository.deleteById(id)
    }

    override fun findAll(pageNumber: Int, size: Int): GetEventCategoriesResponse {
        val pageable = Pageable.from(pageNumber, size)
        val page = eventCategoryRepository.findAll(pageable)
        return GetEventCategoriesResponse(
            page = page.pageable.number,
            size = page.pageable.size,
            totalSize = page.totalSize,
            totalPage = page.totalPages,
            content = page.content
        )
    }
}
