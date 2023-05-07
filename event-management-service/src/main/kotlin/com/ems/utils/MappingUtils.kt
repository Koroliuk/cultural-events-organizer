package com.ems.utils

import com.ems.dto.EventDto
import com.ems.dto.EventFeedbackDto
import com.ems.dto.NotificationDto
import com.ems.dto.UserDto
import com.ems.model.Event
import com.ems.model.EventFeedback
import com.ems.model.Notification
import com.ems.model.User

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

    fun convertToEntity(eventDto: EventDto): Event {
        return Event(
            name = eventDto.name,
            description = eventDto.description,
            startTime = eventDto.startTime,
            endTime = eventDto.endTime,
            eventType = eventDto.eventType,
            location = eventDto.location,
            url = eventDto.url
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
