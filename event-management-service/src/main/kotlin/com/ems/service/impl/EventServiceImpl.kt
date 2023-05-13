package com.ems.service.impl

import com.ems.model.*
import com.ems.repository.EventRepository
import com.ems.service.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.util.Collections
import javax.transaction.Transactional

@Singleton
open class EventServiceImpl(
    @Inject private val eventRepository: EventRepository,
    @Inject private val ticketService: TicketService,
    @Inject private val notificationService: NotificationService,
    @Inject private val feedbackService: EventFeedbackService,
    @Inject private val userService: UserService,
    @Inject private val waitListService: WaitListService
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

    override fun getAttendedEvent(username: String): List<Event> {
        val user = userService.findByUsername(username)
        if (user != null) {
            return ticketService.findPurchasedTicketsByUserId(user.id!!)
                .filter { ticket -> TicketStatus.ACTIVE == ticket.status }
                .filter { ticket -> ticket.event.endTime < LocalDateTime.now() }
                .map { t -> t.event }
                .distinct()
        }
        return Collections.emptyList()
    }

    override fun waitForEventTickets(eventId: Long, username: String) {
        val user = userService.findByUsername(username)
        val event = eventRepository.findById(eventId)
        waitListService.create(event.get(), user!!)
    }

    override fun unWaitForEventTickets(eventId: Long, username: String) {
        val user = userService.findByUsername(username)
        val event = eventRepository.findById(eventId)
        waitListService.deleteByEventAndUser(event.get(), user!!)
    }

    override fun existById(id: Long): Boolean {
        return eventRepository.existsById(id)
    }

    override fun deleteById(id: Long) {
        eventRepository.deleteById(id)
    }

    override fun getByCreatorUsername(username: String): List<Event> {
        return eventRepository.findByCreatorUsername(username)
    }

}
