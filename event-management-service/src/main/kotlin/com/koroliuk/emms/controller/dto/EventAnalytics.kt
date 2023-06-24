package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected


@Introspected
class EventAnalytics(

    val activeTickets: Long,
    val canceledTickets: Long,
    val feedbacks: Long,
    val avrRate: Float

)
