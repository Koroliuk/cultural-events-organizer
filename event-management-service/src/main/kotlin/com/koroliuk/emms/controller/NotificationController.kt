package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.response.NotificationResponseItem
import com.koroliuk.emms.utils.ControllerUtils.getCurrentUser
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.service.NotificationService
import com.koroliuk.emms.service.UserService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.sse.Event
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject
import reactor.core.publisher.Flux
import java.security.Principal
import java.time.LocalDateTime


@Controller("/api/user/notifications")
@Secured(USER)
class NotificationController(
    @Inject private val notificationService: NotificationService,
    @Inject private val userService: UserService
) {

    @Get(produces = [MediaType.TEXT_EVENT_STREAM])
    fun getUserNotifications(@QueryValue since: LocalDateTime?, principal: Principal): Flux<Event<NotificationResponseItem>> {
        val user = getCurrentUser(principal, userService)
        return notificationService.getNotifications(user, since)
            .map { Event.of(it) }
    }

}
