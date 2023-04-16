package com.koroliuk.repository

import com.koroliuk.model.Event
import jakarta.inject.Singleton

@Singleton
class InMemoryEventRepository : EventRepository {
    private val events = mutableListOf<Event>()
    private var nextId = 1L

    override fun create(event: Event): Event {
        val newEvent = event.copy(id = nextId++)
        events.add(newEvent)
        return newEvent
    }

    override fun findById(id: Long): Event? = events.find { it.id == id }

    override fun findAll(): List<Event> = events.toList()

    override fun update(event: Event): Event {
        val index = events.indexOfFirst { it.id == event.id }
        if (index != -1) {
            events[index] = event
            return event
        }
        throw NoSuchElementException("Event with id ${event.id} not found")
    }

    override fun deleteById(id: Long): Boolean = events.removeIf { it.id == id }
}
