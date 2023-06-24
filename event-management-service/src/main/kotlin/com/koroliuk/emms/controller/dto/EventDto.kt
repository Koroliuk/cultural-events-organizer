package com.koroliuk.emms.controller.dto

import com.koroliuk.emms.model.event.EventType
import com.koroliuk.emms.model.event.EventVisibilityType
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import javax.validation.constraints.Future
import javax.validation.constraints.NotBlank


@Introspected
data class EventDto(
    @field:NotBlank
    val name: String,

    @NotBlank
    val description: String,

    @field:Future
    val startTime: LocalDateTime,

    @field:Future
    val endTime: LocalDateTime,

    @field:NotBlank
    val eventType: EventType,

    @field:NotBlank
    val category: String,

    val location: String?,

    val url: String?,

    val visibilityType: EventVisibilityType,

    val organizersGroups: List<Long>,

    val requiredVolunteersAmount: Int?
)
