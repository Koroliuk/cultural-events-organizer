package com.ems.service.impl

import com.ems.model.Event
import com.ems.model.Notification
import com.ems.model.NotificationType
import com.ems.repository.NotificationRepository
import com.ems.service.NotificationService
import com.ems.service.UserService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.LocalDateTime

@Singleton
class NotificationServiceImpl(
    @Inject private val userService: UserService,
    @Inject private val notificationRepository: NotificationRepository
) : NotificationService {

    private val sink: Sinks.Many<Notification> = Sinks.many().multicast().onBackpressureBuffer()

    override fun getNotifications(username: String, since: LocalDateTime?): Flux<Notification> {
        val user = userService.findByUsername(username) ?: throw IllegalArgumentException("No such user")
        val userEvents = if (since != null) {
            notificationRepository.findByUserAndCreatedAfter(user, since)
        } else {
            notificationRepository.findByUser(user)
        }

        return Flux.merge(
            Flux.fromIterable(userEvents),
            sink.asFlux().filter { it.user.username == username }
        )
    }

    override fun addNotificationForUser(username: String, message: String, type: NotificationType, event: Event?): Notification {
        val user = userService.findByUsername(username) ?: throw IllegalArgumentException("No such user")
        val notification = Notification(
            message = message,
            created = LocalDateTime.now(),
            user = user,
            event = event,
            type = type
        )
        sink.tryEmitNext(notification)
        notificationRepository.save(notification)
        return notification
    }
}
