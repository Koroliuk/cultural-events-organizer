package com.koroliuk.service

import com.koroliuk.model.Event
import com.koroliuk.model.Notification
import com.koroliuk.model.NotificationType
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface NotificationService {

    fun getNotifications(username: String, since: LocalDateTime?): Flux<Notification>

    fun addNotificationForUser(username: String, message: String, type: NotificationType, event: Event?): Notification

}
