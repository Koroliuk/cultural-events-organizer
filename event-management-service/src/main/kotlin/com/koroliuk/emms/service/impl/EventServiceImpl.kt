package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.dto.EventInfo
import com.koroliuk.emms.controller.dto.EventAnalytics
import com.koroliuk.emms.controller.dto.EventDto
import com.koroliuk.emms.exception.BlockedEventException
import com.koroliuk.emms.exception.EventNotFoundException
import com.koroliuk.emms.model.attendance.AttendanceEntryStatus
import com.koroliuk.emms.model.event.*
import com.koroliuk.emms.model.notification.NotificationType
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.user.VolunteerApplicationStatus
import com.koroliuk.emms.repository.event.*
import com.koroliuk.emms.repository.user.VolunteerApplicationRepository
import com.koroliuk.emms.service.*
import com.koroliuk.emms.utils.ControllerUtils.formatter
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Singleton
open class EventServiceImpl(
    @Inject private val eventRepository: EventRepository,
    @Inject private val eventMediaRepository: EventMediaRepository,
    @Inject private val offlineEventRepository: OfflineEventRepository,
    @Inject private val onlineEventRepository: OnlineEventRepository,
    @Inject private val attendanceEntryService: AttendanceEntryService,
    @Inject private val feedbackService: EventFeedbackService,
    @Inject private val userService: UserService,
    @Inject private val waitListService: WaitListService,
    @Inject private val privateEventRepository: PrivateEventRepository,
    @Inject private val eventCategoryRepository: EventCategoryRepository,
    @Inject private val eventVolunteerRepository: EventVolunteerRepository,
    @Inject private val groupService: GroupService,
    @Inject private val eventOrganizerRepository: EventOrganizerRepository,
    @Inject private val notificationService: NotificationService,
    @Inject private val volunteerApplicationRepository: VolunteerApplicationRepository
) : EventService {

    @Transactional
    override fun update(event: Event, eventDto: EventDto): EventInfo {
        val eventCategory = eventCategoryRepository.findByName(eventDto.category)
            ?: eventCategoryRepository.save(EventCategory(name = eventDto.category))

        var needToNotify = false

        event.apply {
            if (startTime != eventDto.startTime
                || eventDto.endTime != endTime) {
                needToNotify = true
            }
            name = eventDto.name
            description = eventDto.description
            startTime = eventDto.startTime
            endTime = eventDto.endTime
            category = eventCategory
            visibilityType = eventDto.visibilityType
        }
        val updatedEvent = eventRepository.update(event)

        if (eventDto.requiredVolunteersAmount != null) {
            val volunteers = eventVolunteerRepository.findByEventId(event.id!!)
            if (volunteers == null) {
                eventVolunteerRepository.save(EventVolunteers(amount = eventDto.requiredVolunteersAmount, event = updatedEvent))
            } else {
                volunteers.amount = eventDto.requiredVolunteersAmount
                eventVolunteerRepository.update(volunteers)
            }
        } else {
            eventVolunteerRepository.deleteByEventId(event.id!!)
        }

        if (eventDto.visibilityType == EventVisibilityType.PRIVATE) {
            val privateEvent = privateEventRepository.findById(event.id!!).orElseGet {
                privateEventRepository.save(PrivateEvent(invitationCode = UUID.randomUUID().toString(), event = updatedEvent))
            }
            privateEvent.invitationCode = UUID.randomUUID().toString()
            privateEventRepository.update(privateEvent)
        } else {
            privateEventRepository.deleteById(event.id!!)
        }

        if (eventDto.eventType == EventType.OFFLINE) {
            val offlineEvent = offlineEventRepository.findById(event.id!!).orElseGet {
                needToNotify = true
                offlineEventRepository.save(OfflineEvent(location = eventDto.location!!, event = updatedEvent))
            }
            if (offlineEvent.location != eventDto.location!!) {
                needToNotify = true
            }
            offlineEvent.location = eventDto.location
            offlineEventRepository.update(offlineEvent)
            onlineEventRepository.deleteById(event.id!!)
        } else if (eventDto.eventType == EventType.ONLINE) {
            val onlineEvent = onlineEventRepository.findById(event.id!!).orElseGet {
                needToNotify = true
                onlineEventRepository.save(OnlineEvent(url = eventDto.url!!, event = updatedEvent))
            }
            if (onlineEvent.url != eventDto.url!!) {
                needToNotify = true
            }
            onlineEvent.url = eventDto.url
            onlineEventRepository.save(onlineEvent)
            offlineEventRepository.deleteById(event.id!!)
        }

        val newOrganizers = eventDto.organizersGroups.mapNotNull { groupService.findById(it) }.toSet()
        val currentOrganizers = eventOrganizerRepository.findAllByEventId(event.id!!).map { it.group }.toSet()

        val organizersToAdd = newOrganizers - currentOrganizers
        val organizersToRemove = currentOrganizers - newOrganizers

        organizersToAdd.forEach { group ->
            eventOrganizerRepository.save(EventOrganizer(group = group, event = updatedEvent))
        }

        organizersToRemove.forEach { group ->
            eventOrganizerRepository.deleteByEventAndGroup(updatedEvent, group)
        }

        if (needToNotify) {
            attendanceEntryService.findUsersByEvent(updatedEvent).stream()
                .forEach { user -> notificationService.addNotificationForUser(user, "Updated event", NotificationType.EVENT_UPDATE, updatedEvent, null) }

        }
        return createEventInfo(updatedEvent)
    }

    override fun findEventManagers(id: Long): List<User> {
        return eventOrganizerRepository.findAllByEventId(id).stream()
            .flatMap { it.group.managers.stream() }
            .map { it.user }
            .toList()
    }

    override fun findEventOrganizers(id: Long): List<EventOrganizer> {
        return eventOrganizerRepository.findAllByEventId(id);
    }

    @Transactional
    override fun findById(id: Long): Event? {
        val eventOptional = eventRepository.findById(id)
        if (eventOptional.isEmpty) {
            return null
        }
        return eventOptional.get()
    }

    @Transactional
    override fun create(eventDto: EventDto): EventInfo {
        var category = eventCategoryRepository.findByName(eventDto.name)
        if (category == null) {
            category = eventCategoryRepository.save(EventCategory(
                name = eventDto.name
            ))
        }

        val event = Event(
            name = eventDto.name,
            description = eventDto.description,
            startTime = eventDto.startTime,
            endTime = eventDto.endTime,
            category = category!!,
            visibilityType = eventDto.visibilityType,
        )
        val savedEvent = eventRepository.save(event)

        if (eventDto.visibilityType == EventVisibilityType.PRIVATE) {
            privateEventRepository.save(PrivateEvent(
                event = savedEvent,
                invitationCode = UUID.randomUUID().toString()
            ))
        }

        var off: OfflineEvent? = null
        var on: OnlineEvent? = null
        if (eventDto.eventType == EventType.OFFLINE) {
            off = offlineEventRepository.save(OfflineEvent(
                location = eventDto.location!!,
                event = savedEvent
            ))
        }
        if (eventDto.eventType == EventType.ONLINE) {
            on = onlineEventRepository.save(OnlineEvent(
                url = eventDto.url!!,
                event = savedEvent
            ))
        }

        if (eventDto.requiredVolunteersAmount != null) {
            eventVolunteerRepository.save(EventVolunteers(
                amount = eventDto.requiredVolunteersAmount,
                event = savedEvent
            ))
        }

        for (groupId in eventDto.organizersGroups) {
            val group = groupService.findById(groupId)!!
            eventOrganizerRepository.save(EventOrganizer(
                group = group,
                event = savedEvent
            ))
        }

        val (eventType: EventType, location: String?, url: String?) = getEventOptionaInfo(off, on, savedEvent)

        return EventInfo(
            id = savedEvent.id!!,
            category = savedEvent.category.name,
            name = savedEvent.name,
            description = savedEvent.description,
            startTime = savedEvent.startTime.format(formatter),
            endTime = savedEvent.endTime.format(formatter),
            eventType = eventType,
            location = location,
            url = url,
            visibilityType = savedEvent.visibilityType,
            eventMedia = listOf()
        )
    }

    private fun getEventOptionaInfo(
        off: OfflineEvent?,
        on: OnlineEvent?,
        savedEvent: Event?
    ): Triple<EventType, String?, String?> {
        val eventType: EventType
        val location: String?
        val url: String?
        if (off != null) {
            eventType = EventType.OFFLINE
            location = off.location
            url = null
        } else if (on != null) {
            eventType = EventType.ONLINE
            location = null
            url = on.url
        } else {
            throw EventNotFoundException("Event not found with id: $savedEvent.id!!")
        }
        return Triple(eventType, location, url)
    }


    @Transactional
    override fun getEventInfo(id: Long): EventInfo? {
        val eventOptional = eventRepository.findById(id)
        if (eventOptional.isEmpty) {
            throw EventNotFoundException("Event not found with id: $id")
        }
        val event = eventOptional.get()
        if (event.visibilityType == EventVisibilityType.PRIVATE) {
            throw EventNotFoundException("Event not found with id: $id")
        }
        if (event.blocked) {
            throw BlockedEventException("Event if blocked")
        }
        return createEventInfo(event) ?: throw EventNotFoundException("Event not found with id: $id")
    }

    override fun getEventInfPrivate(id: Long, user: User, invitationCode: String): EventInfo? {
        val eventOptional = eventRepository.findById(id)
        if (eventOptional.isEmpty) {
            throw EventNotFoundException("Event not found with id: $id")
        }
        val event = eventOptional.get()
        if (event.visibilityType != EventVisibilityType.PRIVATE) {
            throw EventNotFoundException("Event not found with id: $id")
        }
        if (event.blocked) {
            throw BlockedEventException("Event if blocked")
        }
        val eventInvitationCode = privateEventRepository.findById(id)
            .map { invitationCode }
            .orElse(null)
        if (invitationCode != eventInvitationCode) {
            throw EventNotFoundException("Wrong invitation code")
        }
        return createEventInfo(event) ?: throw EventNotFoundException("Event not found with id: $id")
    }

     private fun createEventInfo(event: Event): EventInfo {
         val eventMedia = eventMediaRepository.findAllByEventId(event.id!!)
        val offlineEvent = offlineEventRepository.findByEventId(event.id!!)
        val onlineEvent = onlineEventRepository.findByEventId(event.id!!)

         val (eventType: EventType, location: String?, url: String?) = getEventOptionaInfo(offlineEvent, onlineEvent, event)

         return EventInfo(
            id = event.id!!,
            category = event.category.name,
            name = event.name,
            description = event.description,
            startTime = event.startTime.format(formatter),
            endTime = event.endTime.format(formatter),
            eventType = eventType,
            location = location,
            url = url,
            visibilityType = event.visibilityType,
            eventMedia = eventMedia
        )
    }

    override fun findAll(): MutableIterable<Event> {
        return eventRepository.findAll()
    }

    override fun searchEvents(keywords: List<String>?, categories: List<String>?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): MutableIterable<Event> {
        TODO("Not yet implemented")
    }

//    override fun searchEvents(keywords: List<String>?, categories: List<String>?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): MutableIterable<Event> {
//        if (keywords != null) {
//            val keywordsPattern = keywords.joinToString("|", prefix = "(", postfix = ")") { ".*%$it%.*" }
//            return eventRepository.searchEventsByStartTimeBetweenAndKeywords(dateFrom, dateTo, keywordsPattern, categories)
//        }
//        return eventRepository.searchByStartTimeBetween(dateFrom, dateTo, categories)
//    }

    override fun searchEvents1(
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
    ): List<Event> {
        val lowerCaseKeywords = keywords?.map { it.lowercase() }
        val keywordsPattern = lowerCaseKeywords?.joinToString("|", prefix = "(", postfix = ")") { ".*%$it%.*" }
        return eventRepository.searchEvents(keywordsPattern, categories, startTime, endTime, location, eventType)
    }

    override fun getEventAnalytics(event: Event): EventAnalytics {
        return EventAnalytics(
            activeTickets = attendanceEntryService.countByStatusAndEventId(AttendanceEntryStatus.ACTIVE, event.id!!),
            canceledTickets = attendanceEntryService.countByStatusAndEventId(AttendanceEntryStatus.CANCELED, event.id!!),
            feedbacks = feedbackService.countEventIdWithNonEmptyFeedback(event.id!!),
            avrRate = feedbackService.getAvgRateByEventId(event.id!!)
        )
    }

    override fun getAttendedEvent(username: String): List<Event> {
        val user = userService.findByUsername(username)
        if (user != null) {
            return attendanceEntryService.findPurchasedTicketsByUserId(user.id!!)
                .filter { ticket -> AttendanceEntryStatus.ACTIVE == ticket.status }
                .filter { ticket ->
                    val date = if (ticket.attendanceEntryWithLimitedSeats != null) {
                        ticket.attendanceEntryWithLimitedSeats!!.seat.event.endTime
                    } else {
                        ticket.attendanceEntryWithUnlimitedSeats?.priceCategory!!.event.endTime
                    }
                    date < LocalDateTime.now()
                }
                .map { ticket ->
                    val date = if (ticket.attendanceEntryWithLimitedSeats != null) {
                        ticket.attendanceEntryWithLimitedSeats!!.seat.event
                    } else {
                        ticket.attendanceEntryWithUnlimitedSeats?.priceCategory!!.event
                    }
                    date
                }
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

    override fun delete(event: Event) {
        //todo: delete more info with events saved events, evenMedia, complaints, discount, pricacat, seat, notification, ...
        event.eventVolunteers?.let {
            eventVolunteerRepository.delete(it)
        }
        event.privateEvent?.let {
            privateEventRepository.delete(it)
        }
        event.offlineEvent?.let {
            offlineEventRepository.delete(it)
        }
        event.onlineEvent?.let {
            onlineEventRepository.delete(it)
        }
        eventOrganizerRepository.deleteByEventId(event.id!!)
        eventRepository.delete(event)
    }

    override fun findByInvitationCode(invitationCode: String): Event? {
        return eventRepository.findById(25).orElse(null)
    }

    override fun isEventAskingForVolunteers(event: Event): Boolean {
        val volunteersRequest = eventVolunteerRepository.findByEventId(event.id!!)
        val alreadyApproved = volunteerApplicationRepository.findAllByEventAndStatus(event, VolunteerApplicationStatus.APPROVED)
        if (volunteersRequest == null) {
            return false
        }
        return volunteersRequest.amount > alreadyApproved.size
    }

    override fun isEventOrEventGroupBlocked(event: Event): Boolean {
        if (event.blocked) {
            return true
        }
        return eventOrganizerRepository.findAllByEventId(event.id!!).stream()
                .map { it.group }
                .anyMatch { it.isBlocked }
    }

}
