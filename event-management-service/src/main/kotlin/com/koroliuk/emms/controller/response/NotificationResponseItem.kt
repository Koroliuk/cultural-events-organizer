package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.notification.NotificationType
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank


@Introspected
class NotificationResponseItem(

    val message: String,

    val type: NotificationType,

    val eventId: Long? = null,

    val groupId: Long? = null,

    val created: LocalDateTime

)