package com.koroliuk.service.impl

import com.koroliuk.model.Event
import com.koroliuk.repository.EventRepository
import com.koroliuk.service.EventService
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class EventServiceImpl(
    @Inject private val eventRepository: EventRepository
) : EventService {

    override fun create(event: Event): Event {
        return eventRepository.save(event)
    }

    override fun update(event: Event): Event {
        return eventRepository.update(event)
    }

    override fun findById(id: Long): Event {
        val event = eventRepository.findById(id)
        return event.get()
    }

    override fun findAll(): MutableIterable<Event> {
        return eventRepository.findAll()
    }

    override fun searchEventsByKeywords(keywords: List<String>): MutableIterable<Event> {
        val keywordsPattern = keywords.joinToString("|", prefix = "(", postfix = ")") { ".*%$it%.*" }
        return eventRepository.searchEventsByKeywords(if (keywords.isEmpty()) null else keywordsPattern)
    }

    override fun existById(id: Long): Boolean {
        return eventRepository.existsById(id)
    }

    override fun deleteById(id: Long) {
        eventRepository.deleteById(id)
    }

}
