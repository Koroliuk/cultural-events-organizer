package com.ems.service

import com.ems.model.Event
import com.ems.model.User
import com.ems.model.VolunteerApplication

interface VolunteerApplicationService {

    fun create(volunteerApplication: VolunteerApplication)

    fun update(volunteerApplication: VolunteerApplication)

    fun deleteByUserAndEvent(event: Event, user: User)

    fun findByEvent(event: Event): List<VolunteerApplication>

    fun findById(id: Long): VolunteerApplication?

}
