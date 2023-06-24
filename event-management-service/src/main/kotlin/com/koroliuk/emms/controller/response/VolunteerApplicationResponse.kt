package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.user.VolunteerApplicationStatus
import io.micronaut.core.annotation.Introspected


@Introspected
class VolunteerApplicationResponse(

    val id: Long,

    val status: VolunteerApplicationStatus,

    val userId: Long,

    val eventId: Long

)
