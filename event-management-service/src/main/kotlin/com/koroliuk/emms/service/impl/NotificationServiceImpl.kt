package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.NotificationResponseItem
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.notification.Notification
import com.koroliuk.emms.model.notification.NotificationType
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.repository.notification.NotificationRepository
import com.koroliuk.emms.service.NotificationService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.LocalDateTime

@Singleton
class NotificationServiceImpl(
    @Inject private val notificationRepository: NotificationRepository,
) : NotificationService {

    private val sink: Sinks.Many<Notification> = Sinks.many().multicast().onBackpressureBuffer()

    override fun getNotifications(user: User, since: LocalDateTime?): Flux<NotificationResponseItem> {
        val userEvents = if (since != null) {
            notificationRepository.findByUserAndCreatedAfter(user, since)
        } else {
            notificationRepository.findByUser(user)
        }
        return Flux.merge(
            Flux.fromIterable(userEvents),
            sink.asFlux().filter { it.user.username == user.username }
        ).map { notification ->
            var eventId: Long? = null
            var groupId: Long? = null
            notification.event?.let {
                eventId = it.id!!
            }
            notification.group?.let {
                groupId = it.id!!
            }
            NotificationResponseItem(
                message = notification.message,
                type = notification.type,
                eventId = eventId,
                groupId = groupId,
                created = notification.created
            )
        }
    }

    override fun addNotificationForUser(
        user: User,
        message: String,
        type: NotificationType,
        event: Event?,
        group: Group?
    ) {
        val notification = Notification(
            message = message,
            created = LocalDateTime.now(),
            user = user,
            event = event,
            group = group,
            type = type
        )
        notificationRepository.save(notification)
        sink.tryEmitNext(notification)
    }

    override fun deleteByGroupId(groupId: Long) {
        notificationRepository.deleteByGroupId(groupId)
    }
}
