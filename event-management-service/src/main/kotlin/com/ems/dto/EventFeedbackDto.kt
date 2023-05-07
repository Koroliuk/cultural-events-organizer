package com.ems.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Introspected
class EventFeedbackDto (

    @NonNull
    val eventId: Long,

    @Max(value = 10)
    @Min(value = 0)
    @NonNull
    val rate: Int,

    val feedback: String? = null,

)
