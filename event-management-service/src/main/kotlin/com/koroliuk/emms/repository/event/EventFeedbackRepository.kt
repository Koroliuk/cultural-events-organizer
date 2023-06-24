package com.koroliuk.emms.repository.event

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventFeedback
import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository

@Repository
interface EventFeedbackRepository : CrudRepository<EventFeedback, Long> {

    fun getAvgRateByEventId(id: Long): Float

    fun findByEventIdOrderById(id: Long, pageable: Pageable): Page<EventFeedback>

    fun countByEventIdAndTextIsNotNullAndTextIsNotEmpty(id: Long): Long

    fun existsByUserAndEvent(user: User, event: Event): Boolean

}
