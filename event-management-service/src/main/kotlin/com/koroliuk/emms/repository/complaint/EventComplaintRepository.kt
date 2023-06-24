package com.koroliuk.emms.repository.complaint

import com.koroliuk.emms.model.complaint.EventComplaint
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface EventComplaintRepository : CrudRepository<EventComplaint, Long> {

    fun existsByComplaintId(complaintId: Long): Boolean

    fun findByComplaintId(complaintId: Long): EventComplaint?

}
