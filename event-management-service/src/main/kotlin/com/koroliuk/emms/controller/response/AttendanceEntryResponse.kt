package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.attendance.AttendanceEntryStatus
import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import java.time.LocalDateTime


@Introspected
class AttendanceEntryResponse(

    val purchaseTime: LocalDateTime,
    val status : AttendanceEntryStatus,
    val priceToPay: BigDecimal,
    val eventId: Long,
    val seatNumber: Long?

)
