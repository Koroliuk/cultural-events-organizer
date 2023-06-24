package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.event.Event
import io.micronaut.core.annotation.Introspected


@Introspected
class SearchResponse(

    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val elements: List<Event>

)
