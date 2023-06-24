package com.koroliuk.emms.controller.response

import com.koroliuk.emms.controller.request.SeatDto
import io.micronaut.core.annotation.Introspected


@Introspected
class GetSeatsResponse(

    val page: Int,
    val size: Int,
    val totalSize: Long,
    val totalPage: Int,
    val content: List<SeatDto>

)
