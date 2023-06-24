package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.GetDiscountCodesResponse
import com.koroliuk.emms.controller.response.DiscountCodeResponse
import com.koroliuk.emms.model.attendance.DiscountCode
import com.koroliuk.emms.model.attendance.DiscountType
import com.koroliuk.emms.model.event.Event
import java.time.LocalDateTime

interface DiscountCodeService {

    fun createDiscountCode(
        event: Event,
        code: String,
        type: DiscountType,
        value: Long,
        expiredDate: LocalDateTime
    ): DiscountCodeResponse

    fun getDiscountCodeInfo(id: Long): DiscountCodeResponse?
    fun deleteDiscountCode(discountCode: DiscountCode)

    fun findById(id: Long): DiscountCode?

    fun findByCode(code: String): DiscountCode?

    fun findByEvent(event: Event, page: Int, size: Int): GetDiscountCodesResponse

}
