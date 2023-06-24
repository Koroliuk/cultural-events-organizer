package com.koroliuk.emms.utils

import com.koroliuk.emms.controller.request.EventCreateRequest
import com.koroliuk.emms.controller.response.VolunteerApplicationResponse
import com.koroliuk.emms.controller.response.DiscountCodeResponse
import com.koroliuk.emms.model.attendance.DiscountCode
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventFeedback
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.user.VolunteerApplication
import java.time.LocalDateTime

object ConversionUtils {

    fun convertVolunteerApplicationForResponse(volunteerApplication: VolunteerApplication): VolunteerApplicationResponse {
        return VolunteerApplicationResponse(
            id = volunteerApplication.id!!,
            status = volunteerApplication.status,
            userId = volunteerApplication.user.id!!,
            eventId = volunteerApplication.event.id!!
        )
    }

    fun convertDiscountCodeToDiscountCodeResponse(discountCode: DiscountCode): DiscountCodeResponse {
        return DiscountCodeResponse(
            eventId = discountCode.event.id!!,
            code = discountCode.code,
            type = discountCode.type,
            value = discountCode.value,
            expiredDate = discountCode.expirationDate,
            id = discountCode.id!!
        )
    }

    fun convertToEntity(eventCreateRequest: EventCreateRequest, user: User, event: Event): EventFeedback {
        return EventFeedback(
            event = event,
            user = user,
            rate = eventCreateRequest.rate,
            text = eventCreateRequest.feedback,
            created = LocalDateTime.now()
        )
    }

}