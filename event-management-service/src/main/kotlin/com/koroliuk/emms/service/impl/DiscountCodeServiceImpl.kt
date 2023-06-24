package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.GetDiscountCodesResponse
import com.koroliuk.emms.controller.response.DiscountCodeResponse
import com.koroliuk.emms.model.attendance.DiscountCode
import com.koroliuk.emms.model.attendance.DiscountType
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.repository.attendance.DiscountCodeRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import com.koroliuk.emms.service.DiscountCodeService
import com.koroliuk.emms.utils.ConversionUtils.convertDiscountCodeToDiscountCodeResponse
import io.micronaut.data.model.Pageable
import java.time.LocalDateTime

@Singleton
class DiscountCodeServiceImpl(
    @Inject private val discountCodeRepository: DiscountCodeRepository
) : DiscountCodeService {

    override fun createDiscountCode(
        event: Event,
        code: String,
        type: DiscountType,
        value: Long,
        expiredDate: LocalDateTime
    ): DiscountCodeResponse {
        val discountCode = DiscountCode(code = code, type = type, value = value, expirationDate = expiredDate, event = event)
        return convertDiscountCodeToDiscountCodeResponse(discountCodeRepository.save(discountCode))
    }

    override fun getDiscountCodeInfo(id: Long): DiscountCodeResponse? {
        return discountCodeRepository.findById(id)
            .map { convertDiscountCodeToDiscountCodeResponse(it) }
            .orElse(null)
    }


    override fun deleteDiscountCode(discountCode: DiscountCode) {
        discountCodeRepository.delete(discountCode)
    }

    override fun findById(id: Long): DiscountCode? {
        val discountCode = discountCodeRepository.findById(id)
        return if (discountCode.isPresent) {
            discountCode.get()
        } else {
            null
        }
    }

    override fun findByCode(code: String): DiscountCode? {
        return discountCodeRepository.findByCode(code)
    }

    override fun findByEvent(event: Event, page: Int, size: Int): GetDiscountCodesResponse {
        val pageable: Pageable = Pageable.from(page, size)
        val discountCodesPage = discountCodeRepository.findAllByEvent(event, pageable)
        return GetDiscountCodesResponse(
            page = discountCodesPage.pageable.number,
            size = discountCodesPage.size,
            totalSize = discountCodesPage.totalSize,
            totalPage = discountCodesPage.totalPages,
            content = discountCodesPage.content.stream()
                .map { convertDiscountCodeToDiscountCodeResponse(it) }
                .toList()
        )
    }

}
