package com.ems.repository

import com.ems.model.Event
import com.ems.model.User
import com.ems.model.VolunteerApplication
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface VolunteerApplicationRepository : CrudRepository<VolunteerApplication, Long> {

    fun deleteByEventAndUser(event: Event, user: User)

    fun findByEvent(event: Event): List<VolunteerApplication>

}
