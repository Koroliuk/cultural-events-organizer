package com.koroliuk.emms.controller.dto

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventCategory
import io.micronaut.core.annotation.Introspected


@Introspected
class EventAnalyticsEnvelope(
    val event: Event,
    val category: EventCategory,
    val eventAnalytics: EventAnalytics
)
