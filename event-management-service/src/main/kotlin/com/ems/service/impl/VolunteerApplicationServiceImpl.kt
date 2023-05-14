package com.ems.service.impl

import com.ems.model.Event
import com.ems.model.User
import com.ems.model.VolunteerApplication
import com.ems.repository.VolunteerApplicationRepository
import com.ems.service.VolunteerApplicationService
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class VolunteerApplicationServiceImpl(
    @Inject private val volunteerApplicationRepository: VolunteerApplicationRepository
) : VolunteerApplicationService {
    override fun create(volunteerApplication: VolunteerApplication) {
        volunteerApplicationRepository.save(volunteerApplication)
    }

    override fun update(volunteerApplication: VolunteerApplication) {
        volunteerApplicationRepository.update(volunteerApplication)
    }

    override fun deleteByUserAndEvent(event: Event, user: User) {
        volunteerApplicationRepository.deleteByEventAndUser(event, user)
    }

    override fun findByEvent(event: Event): List<VolunteerApplication> {
        return volunteerApplicationRepository.findByEvent(event)
    }

    override fun findById(id: Long): VolunteerApplication? {
        return volunteerApplicationRepository.findById(id)
            .orElse(null)
    }

}
