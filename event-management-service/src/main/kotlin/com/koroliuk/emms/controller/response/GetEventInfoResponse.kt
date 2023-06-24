package com.koroliuk.emms.controller.response

import com.koroliuk.emms.controller.dto.ComplaintInfo
import com.koroliuk.emms.controller.dto.EventInfo
import io.micronaut.core.annotation.Introspected


@Introspected
class GetEventInfoResponse(

    val page: Int,
    val size: Int,
    val totalSize: Long,
    val totalPage: Int,
    val content: List<EventInfo>

)
