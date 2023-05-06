package com.koroliuk.service.impl

import com.koroliuk.model.Event
import com.koroliuk.model.NotificationType
import com.koroliuk.repository.EventRepository
import com.koroliuk.service.EventService
import com.koroliuk.service.NotificationService
import com.koroliuk.service.TicketService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class EventServiceImpl(
    @Inject private val eventRepository: EventRepository,
    @Inject private val ticketService: TicketService,
    @Inject private val notificationService: NotificationService
) : EventService {

    override fun create(event: Event): Event {
        return eventRepository.save(event)
    }

    @Transactional
    override fun update(event: Event): Event {
        val updatedEvent = eventRepository.update(event)
        ticketService.findUsersByEvent(updatedEvent).stream()
            .forEach { user -> notificationService.addNotificationForUser(user.username, "Updated event", NotificationType.EVENT_UPDATE, event) }
        return updatedEvent
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
