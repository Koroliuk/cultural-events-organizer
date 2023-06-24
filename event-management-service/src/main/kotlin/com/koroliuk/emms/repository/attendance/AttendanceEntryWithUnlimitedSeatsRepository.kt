package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.attendance.AttendanceEntryWithUnlimitedSeats
import com.koroliuk.emms.model.event.Event
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AttendanceEntryWithUnlimitedSeatsRepository : CrudRepository<AttendanceEntryWithUnlimitedSeats, Long> {

    @Query("SELECT a FROM AttendanceEntryWithUnlimitedSeats a WHERE a.priceCategory.event = :event")
    fun findAllByPriceCategoryEvent(event: Event): List<AttendanceEntryWithUnlimitedSeats>

}