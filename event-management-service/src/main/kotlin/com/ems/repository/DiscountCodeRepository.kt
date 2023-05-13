package com.ems.repository

import com.ems.model.DiscountCode
import com.ems.model.Event
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface DiscountCodeRepository : CrudRepository<DiscountCode, Long> {

    fun findByCode(code: String): DiscountCode?

    fun findByEvent(event: Event): List<DiscountCode>

}
