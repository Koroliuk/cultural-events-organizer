package com.ems.utils

import com.ems.dto.*
import com.ems.model.*

object MappingUtils {

    fun convertToEntity(userDto: UserDto): User {
        return User(
            username = userDto.username,
            passwordHash = userDto.passwordHash,
            email = userDto.email,
            firstName = userDto.firstName,
            lastName = userDto.lastName
        )
    }

    fun convertToEntity(eventDto: EventDto, category: EventCategory, users: MutableSet<User>): Event {
        return Event(
            name = eventDto.name,
            description = eventDto.description,
            startTime = eventDto.startTime,
            endTime = eventDto.endTime,
            eventType = eventDto.eventType,
            location = eventDto.location,
            url = eventDto.url,
            category = category,
            creators = users,
            isTicketsLimited = eventDto.isTicketsLimited,
            maxTickets = eventDto.maxTickets,
            price = eventDto.price,
            isPrivate = eventDto.isPrivate
        )
    }

    fun convertToEntity(eventFeedbackDto: EventFeedbackDto, user: User, event: Event): EventFeedback {
        return EventFeedback(
            event = event,
            user = user,
            rate = eventFeedbackDto.rate,
            feedback = eventFeedbackDto.feedback
        )
    }

    fun convertToEntity(eventCategoryDto: EventCategoryDto): EventCategory {
        return EventCategory(
            name = eventCategoryDto.name,
            description = eventCategoryDto.description
        )
    }

    fun convertToDto(notification: Notification): NotificationDto {
        return NotificationDto(
            message = notification.message,
            type = notification.type.toString(),
            eventId = if (notification.event != null) {
                notification.event!!.id
            } else {
                null
            }
        )
    }

}
