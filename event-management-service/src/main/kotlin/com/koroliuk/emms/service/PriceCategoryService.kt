package com.koroliuk.emms.service

import com.koroliuk.emms.controller.request.PriceCategoryDto
import com.koroliuk.emms.controller.request.PriceCategoryUpdateDto
import com.koroliuk.emms.controller.response.GetPriceCategoryResponse
import com.koroliuk.emms.model.attendance.PriceCategory
import com.koroliuk.emms.model.event.Event
import io.micronaut.data.model.Pageable

interface PriceCategoryService {

    fun getAllByEvent(event: Event, pageable: Pageable): GetPriceCategoryResponse

    fun existsById(priceCategoryId: Long): Boolean

    fun getById(id: Long): PriceCategoryDto?

    fun findById(id: Long): PriceCategory?

    fun delete(id: Long)

    fun create(event: Event, priceCategoryDTO: PriceCategoryDto): PriceCategoryDto

    fun update(priceCategory: PriceCategory, priceCategoryDTO: PriceCategoryUpdateDto): PriceCategoryDto

}