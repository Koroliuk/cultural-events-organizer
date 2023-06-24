package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.NotificationResponseItem
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.notification.NotificationType
import com.koroliuk.emms.model.user.User
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface NotificationService {

    fun getNotifications(user: User, since: LocalDateTime?): Flux<NotificationResponseItem>

    fun addNotificationForUser(user: User, message: String, type: NotificationType, event: Event?, group: Group?)

    fun deleteByGroupId(groupId: Long)

}
