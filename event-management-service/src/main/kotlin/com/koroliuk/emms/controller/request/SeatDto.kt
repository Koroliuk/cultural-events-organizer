package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected


@Introspected
class SeatDto(
    val number: Int,
    val eventId: Long? = null,
    val priceCategoryId: Long,
    var id: Long? = null
)