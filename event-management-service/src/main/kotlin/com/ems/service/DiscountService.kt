package com.ems.service

import com.ems.model.DiscountCode
import com.ems.model.Event

interface DiscountCodeService {
    fun createDiscountCode(event: Event, code: String, discountPercentage: Float): DiscountCode
    fun deleteDiscountCode(id: Long)
    fun findByCode(code: String): DiscountCode?
    fun findByEvent(event: Event): List<DiscountCode>
}
