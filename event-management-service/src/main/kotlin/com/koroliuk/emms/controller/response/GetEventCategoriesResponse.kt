package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.event.EventCategory
import io.micronaut.core.annotation.Introspected


@Introspected
class GetEventCategoriesResponse(

    val page: Int,

    val size: Int,

    val totalSize: Long,

    val totalPage: Int,

    val content: List<EventCategory>

)
