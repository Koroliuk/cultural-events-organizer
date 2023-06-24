package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.request.PriceCategoryDto
import com.koroliuk.emms.controller.request.PriceCategoryUpdateDto
import com.koroliuk.emms.controller.response.GetPriceCategoryResponse
import com.koroliuk.emms.model.attendance.PriceCategory
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.repository.attendance.PriceCategoryRepository
import com.koroliuk.emms.service.PriceCategoryService
import io.micronaut.core.util.StringUtils
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PriceCategoryServiceImpl(
    @Inject val priceCategoryRepository: PriceCategoryRepository
): PriceCategoryService {

    override fun getAllByEvent(event: Event, pageable: Pageable): GetPriceCategoryResponse {
        val result = priceCategoryRepository.findAllByEventId(event.id!!, pageable)
        return GetPriceCategoryResponse(
            page = result.pageable.number,
            size = result.pageable.size,
            totalSize = result.totalSize,
            totalPage = result.totalPages,
            content = result.content.stream()
                .map { priceCategoryToDto(it) }
                .toList()
        )
    }

    override fun existsById(priceCategoryId: Long): Boolean {
        return priceCategoryRepository.existsById(priceCategoryId)
    }


    override fun getById(id: Long): PriceCategoryDto? {
        return priceCategoryRepository.findById(id)
            .map { priceCategoryToDto(it) }
            .orElse(null)
    }

    override fun findById(id: Long): PriceCategory? {
        return priceCategoryRepository.findById(id).orElse(null)
    }

    override fun delete(id: Long) {
        priceCategoryRepository.deleteById(id)
    }

    override fun create(event: Event, priceCategoryDTO: PriceCategoryDto): PriceCategoryDto {
        val priceCategory = PriceCategory(priceCategoryDTO.name, priceCategoryDTO.price, event)
        return priceCategoryToDto(priceCategoryRepository.save(priceCategory))
    }

    override fun update(priceCategory: PriceCategory, priceCategoryDTO: PriceCategoryUpdateDto): PriceCategoryDto {
        if (!StringUtils.isEmpty(priceCategoryDTO.name)) {
            priceCategory.name = priceCategoryDTO.name!!
        }
        if (priceCategoryDTO.price != null) {
            priceCategory.price = priceCategoryDTO.price!!
        }
        return priceCategoryToDto(priceCategoryRepository.update(priceCategory))
    }

    private fun priceCategoryToDto(priceCategory: PriceCategory): PriceCategoryDto {
        return PriceCategoryDto(
            id = priceCategory.id,
            eventId = priceCategory.event.id!!,
            name = priceCategory.name,
            price = priceCategory.price
        )
    }
}
