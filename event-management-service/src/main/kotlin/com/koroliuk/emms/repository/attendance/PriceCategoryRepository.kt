package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.attendance.PriceCategory
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable


@Repository
interface PriceCategoryRepository : JpaRepository<PriceCategory, Long> {

    fun findAllByEventId(eventId: Long, pageable: Pageable): Page<PriceCategory>

}
