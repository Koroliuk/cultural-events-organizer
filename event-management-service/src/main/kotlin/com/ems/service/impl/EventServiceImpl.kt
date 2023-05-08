package com.ems.service.impl

import com.ems.model.*
import com.ems.repository.EventRepository
import com.ems.service.EventFeedbackService
import com.ems.service.EventService
import com.ems.service.NotificationService
import com.ems.service.TicketService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime
import javax.transaction.Transactional

@Singleton
open class EventServiceImpl(
    @Inject private val eventRepository: EventRepository,
    @Inject private val ticketService: TicketService,
    @Inject private val notificationService: NotificationService,
    @Inject private val feedbackService: EventFeedbackService
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

    override fun searchEvents(keywords: List<String>?, categories: List<String>?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): MutableIterable<Event> {
        if (keywords != null) {
            val keywordsPattern = keywords.joinToString("|", prefix = "(", postfix = ")") { ".*%$it%.*" }
            return eventRepository.searchEventsByStartTimeBetweenAndKeywords(dateFrom, dateTo, keywordsPattern, categories)
        }
        return eventRepository.searchByStartTimeBetween(dateFrom, dateTo, categories)
    }

    override fun getEventAnalytics(event: Event): EventAnalytics {
        return EventAnalytics(
            activeTickets = ticketService.countByStatusAndEventId(TicketStatus.ACTIVE, event.id!!),
            canceledTickets = ticketService.countByStatusAndEventId(TicketStatus.CANCELED, event.id!!),
            feedbacks = feedbackService.countEventIdWithNonEmptyFeedback(event.id!!),
            avrRate = feedbackService.getAvgRateByEventId(event.id!!)
        )
    }

    override fun getEventsAnalytics(dateFrom: LocalDateTime?, dateTo: LocalDateTime?): List<EventAnalyticsEnvelope> {
        return searchEvents(null, null, dateFrom, dateTo)
            .filter { event -> event.endTime < LocalDateTime.now() }
            .map {
                EventAnalyticsEnvelope(
                    event = it,
                    category = it.category,
                    eventAnalytics = getEventAnalytics(it)
                )
            }
    }

    override fun existById(id: Long): Boolean {
        return eventRepository.existsById(id)
    }

    override fun deleteById(id: Long) {
        eventRepository.deleteById(id)
    }

}
