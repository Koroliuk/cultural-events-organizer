package com.koroliuk.utils

import com.koroliuk.dto.EventDto
import com.koroliuk.dto.NotificationDto
import com.koroliuk.dto.UserDto
import com.koroliuk.model.Event
import com.koroliuk.model.Notification
import com.koroliuk.model.User

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
