package com.koroliuk.emms.controller.dto

import com.koroliuk.emms.model.complaint.ComplaintStatus
import io.micronaut.core.annotation.Introspected


@Introspected
class ComplaintInfo(

    val authorUsername: String,

    val reason: String?,

    val status: ComplaintStatus,

    val eventId: Long?,

    val commentId: Long?,

    val id: Long
)
