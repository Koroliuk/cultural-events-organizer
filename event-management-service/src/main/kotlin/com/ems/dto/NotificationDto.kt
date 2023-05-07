package com.ems.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class NotificationDto (

    @NotBlank
    val message: String,

    @NotBlank
    val type: String,

    val eventId: Long? = null

)
