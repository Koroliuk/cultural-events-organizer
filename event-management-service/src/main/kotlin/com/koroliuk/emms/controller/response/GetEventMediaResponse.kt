package com.koroliuk.emms.controller.response

import com.koroliuk.emms.model.event.EventMedia
import io.micronaut.core.annotation.Introspected


@Introspected
class GetEventMediaResponse(

    val page: Int,
    val size: Int,
    val totalSize: Long,
    val totalPage: Int,
    val content: List<EventMedia>

)
