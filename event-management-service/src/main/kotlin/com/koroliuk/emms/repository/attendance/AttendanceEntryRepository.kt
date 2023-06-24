package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.attendance.AttendanceEntry
import com.koroliuk.emms.model.attendance.AttendanceEntryStatus
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository


@Repository
interface AttendanceEntryRepository : CrudRepository<AttendanceEntry, Long> {

    fun findByUserId(userId: Long): List<AttendanceEntry>

    fun findByUserId(userId: Long, pageable: Pageable): Page<AttendanceEntry>

    fun findByStatus(status: AttendanceEntryStatus): List<AttendanceEntry>

}
