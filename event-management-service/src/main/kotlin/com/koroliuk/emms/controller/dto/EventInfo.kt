package com.koroliuk.emms.controller.dto

import com.koroliuk.emms.model.event.EventMedia
import com.koroliuk.emms.model.event.EventType
import com.koroliuk.emms.model.event.EventVisibilityType
import io.micronaut.core.annotation.Introspected


@Introspected
class EventInfo(

    val id: Long,
    val category: String,
    val name: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val eventType: EventType,
    val location: String?,
    val url: String?,
    val visibilityType: EventVisibilityType,
    val eventMedia: List<EventMedia>

)
