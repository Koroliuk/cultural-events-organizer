package com.koroliuk.emms.service

import com.koroliuk.emms.controller.dto.EventInfo
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.controller.dto.EventAnalytics
import com.koroliuk.emms.controller.dto.EventDto
import com.koroliuk.emms.model.event.EventOrganizer
import com.koroliuk.emms.model.event.EventType
import com.koroliuk.emms.model.user.User
import java.time.LocalDateTime

interface EventService {

    fun getEventInfo(id: Long): EventInfo?

    fun getEventInfPrivate(id: Long, user: User, invitationCode: String): EventInfo?


    fun findEventManagers(id: Long): List<User>

    fun findEventOrganizers(id: Long): List<EventOrganizer>

    fun findById(id: Long): Event?

    fun create(event: EventDto): EventInfo

    fun update(event: Event,eventDto: EventDto): EventInfo

    fun findAll(): MutableIterable<Event>

    fun searchEvents(keywords: List<String>?, categories: List<String>?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): MutableIterable<Event>

    fun searchEvents1(
        keywords: List<String>?,
        categories: List<String>?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
        location: String?,
        eventType: EventType?,
        page: Int,
        size: Int,
        sort: String,
        sortType: String
    ): List<Event>

    fun getEventAnalytics(event: Event): EventAnalytics

    fun getAttendedEvent(username: String): List<Event>

    fun waitForEventTickets(eventId: Long, username: String)

    fun unWaitForEventTickets(eventId: Long, username: String)

    fun existById(id: Long): Boolean

    fun delete(event: Event)

    fun findByInvitationCode(invitationCode: String): Event?

    fun isEventAskingForVolunteers(event: Event): Boolean

    fun isEventOrEventGroupBlocked(event: Event): Boolean

}
