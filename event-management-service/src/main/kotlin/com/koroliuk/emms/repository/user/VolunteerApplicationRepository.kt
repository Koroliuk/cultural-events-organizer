package com.koroliuk.emms.repository.user

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.user.VolunteerApplication
import com.koroliuk.emms.model.user.VolunteerApplicationStatus
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.repository.CrudRepository


@Repository
interface VolunteerApplicationRepository : CrudRepository<VolunteerApplication, Long> {

    fun findAllByEvent(event: Event, pageable: Pageable): Page<VolunteerApplication>

    fun findAllByEventAndStatus(event: Event, status: VolunteerApplicationStatus): List<VolunteerApplication>

    fun findAllByUser(user: User, pageable: Pageable): Page<VolunteerApplication>

}
