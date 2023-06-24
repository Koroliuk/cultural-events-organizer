package com.koroliuk.emms.controller.response

import com.koroliuk.emms.controller.dto.GroupInfo
import io.micronaut.core.annotation.Introspected


@Introspected
class GetGroupResponse(

    val page: Int,
    val size: Int,
    val totalSize: Long,
    val totalPage: Int,
    val content: List<GroupInfo>

)
