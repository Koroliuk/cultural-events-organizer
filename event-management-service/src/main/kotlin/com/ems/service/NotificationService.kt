package com.ems.service

import com.ems.model.Event
import com.ems.model.Notification
import com.ems.model.NotificationType
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface NotificationService {

    fun getNotifications(username: String, since: LocalDateTime?): Flux<Notification>

    fun addNotificationForUser(username: String, message: String, type: NotificationType, event: Event?): Notification

}
