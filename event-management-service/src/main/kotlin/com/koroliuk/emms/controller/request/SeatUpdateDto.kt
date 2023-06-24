package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected


@Introspected
class SeatUpdateDto(

    val number: Int?,

    val priceCategoryId: Long?,

)