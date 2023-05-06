package com.koroliuk.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class NotificationDto (

    @NotBlank
    val message: String

)
