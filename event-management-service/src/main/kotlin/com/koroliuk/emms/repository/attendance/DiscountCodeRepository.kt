package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.attendance.DiscountCode
import com.koroliuk.emms.model.event.Event
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository

@Repository
interface DiscountCodeRepository : CrudRepository<DiscountCode, Long> {

    fun findByCode(code: String): DiscountCode?

    fun findAllByEvent(event: Event, pageable: Pageable): Page<DiscountCode>

}
