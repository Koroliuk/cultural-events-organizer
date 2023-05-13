package com.ems.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull


@Introspected
class ComplaintDto(

    @NonNull
    val eventId: Long,

    val text: String

)
