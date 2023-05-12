package com.ems.service.impl

import com.ems.model.DiscountCode
import com.ems.model.Event
import com.ems.repository.DiscountCodeRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import com.ems.service.DiscountCodeService

@Singleton
class DiscountCodeServiceImpl(
    @Inject private val discountCodeRepository: DiscountCodeRepository
) : DiscountCodeService {

    override fun createDiscountCode(event: Event, code: String, discountPercentage: Float): DiscountCode {
        val discountCode = DiscountCode(code, discountPercentage, event)
        return discountCodeRepository.save(discountCode)
    }

    override fun deleteDiscountCode(id: Long) {
        discountCodeRepository.deleteById(id)
    }

    override fun findByCode(code: String): DiscountCode? {
        return discountCodeRepository.findByCode(code)
    }

    override fun findByEvent(event: Event): List<DiscountCode> {
        return discountCodeRepository.findByEvent(event)
    }

}
