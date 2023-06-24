package com.koroliuk.emms.controller.response

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import java.time.LocalDateTime
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Introspected
class EventFeedbackResponse (

    val eventId: Long,

    val rate: Int,

    val createdDate: String,

    val feedback: String? = null,

)
