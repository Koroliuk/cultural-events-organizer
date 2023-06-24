package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.GetVolunteerApplicationsResponse
import com.koroliuk.emms.controller.response.VolunteerApplicationResponse
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.user.VolunteerApplication
import com.koroliuk.emms.repository.user.VolunteerApplicationRepository
import com.koroliuk.emms.service.VolunteerApplicationService
import com.koroliuk.emms.utils.ConversionUtils
import com.koroliuk.emms.utils.ConversionUtils.convertVolunteerApplicationForResponse
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class VolunteerApplicationServiceImpl(
    @Inject private val volunteerApplicationRepository: VolunteerApplicationRepository
) : VolunteerApplicationService {
    override fun create(event: Event, user: User): VolunteerApplicationResponse {
        val volunteerApplication = VolunteerApplication(event = event, user = user)
        val saved = volunteerApplicationRepository.save(volunteerApplication)
        return convertVolunteerApplicationForResponse(saved)
    }

    override fun update(volunteerApplication: VolunteerApplication) {
        volunteerApplicationRepository.update(volunteerApplication)
    }

    override fun deleteById(id: Long) {
        volunteerApplicationRepository.deleteById(id)
    }

    override fun findByEvent(event: Event, page: Int, size: Int): GetVolunteerApplicationsResponse {
        val pageable = Pageable.from(page, size)
        val volunteerApplicationsPage = volunteerApplicationRepository.findAllByEvent(event, pageable)
        return GetVolunteerApplicationsResponse(
            page = volunteerApplicationsPage.pageable.number,
            size = volunteerApplicationsPage.size,
            totalSize = volunteerApplicationsPage.totalSize,
            totalPage = volunteerApplicationsPage.totalPages,
            content = volunteerApplicationsPage.content.stream()
                .map { convertVolunteerApplicationForResponse(it) }
                .toList()
        )
    }

    override fun findById(id: Long): VolunteerApplication? {
        return volunteerApplicationRepository.findById(id)
            .orElse(null)
    }

}
