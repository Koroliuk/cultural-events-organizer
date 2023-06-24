package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.GetVolunteerApplicationsResponse
import com.koroliuk.emms.controller.response.VolunteerApplicationResponse
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.user.VolunteerApplication

interface VolunteerApplicationService {

    fun create(event: Event, user: User): VolunteerApplicationResponse

    fun update(volunteerApplication: VolunteerApplication)

    fun deleteById(id: Long)

    fun findByEvent(event: Event, page: Int, size: Int): GetVolunteerApplicationsResponse

    fun findById(id: Long): VolunteerApplication?

}
