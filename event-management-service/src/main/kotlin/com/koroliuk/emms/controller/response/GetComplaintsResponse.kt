package com.koroliuk.emms.controller.response

import com.koroliuk.emms.controller.dto.ComplaintInfo
import io.micronaut.core.annotation.Introspected


@Introspected
class GetComplaintsResponse(

    val page: Int,
    val size: Int,
    val totalSize: Long,
    val totalPage: Int,
    val content: List<ComplaintInfo>

)
