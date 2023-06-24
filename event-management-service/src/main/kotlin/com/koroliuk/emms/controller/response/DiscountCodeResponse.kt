package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.attendance.DiscountType
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime


@Introspected
class DiscountCodeResponse(

    val eventId: Long,
    val code: String,
    val type: DiscountType,
    val value: Long,
    val expiredDate: LocalDateTime,
    val id: Long

)
