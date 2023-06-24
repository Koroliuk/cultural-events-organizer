package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull


@Introspected
class EventCategoryDto(

        @NonNull
        val name: String

)
