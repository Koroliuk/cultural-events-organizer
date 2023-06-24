package com.koroliuk.emms.utils

import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.service.EventService
import com.koroliuk.emms.service.UserService
import java.security.Principal
import java.time.format.DateTimeFormatter

object ControllerUtils {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    const val DEFAULT_PAGE = "0"
    const val DEFAULT_PAGE_SIZE = "10"

    fun getCurrentUser(principal: Principal, userService: UserService): User {
        return userService.findByUsername(principal.name)!!
    }

    fun isManagerOfEvent(eventId: Long, principal: Principal, eventService: EventService): Boolean {
        val managers = eventService.findEventManagers(eventId)
        return managers.stream()
            .anyMatch { u -> principal.name == u.username }
    }

    fun createMessageResponse(message: String?): Map<String, String> {
        if (message != null) {
            return mapOf("message" to message)
        }
        return mapOf()
    }

}