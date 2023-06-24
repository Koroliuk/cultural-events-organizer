package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.attendance.Seat
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable

@Repository
interface SeatRepository : JpaRepository<Seat, Long> {

    fun findAllByEventId(eventId: Long): List<Seat>

    fun findAllByEventId(eventId: Long, pageable: Pageable): Page<Seat>

}
