package com.ems.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull


@Introspected
class EventCategoryDto(

        @NonNull
        val name: String,

        val description: String

)
