package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.attendance.AttendanceEntryWithLimitedSeats
import com.koroliuk.emms.model.event.Event
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AttendanceEntryWithLimitedSeatsRepository : CrudRepository<AttendanceEntryWithLimitedSeats, Long> {

    @Query("SELECT a FROM AttendanceEntryWithLimitedSeats a WHERE a.seat.event = :event")
    fun findAllBySeatEvent(event: Event): List<AttendanceEntryWithLimitedSeats>
}