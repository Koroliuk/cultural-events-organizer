package com.koroliuk.emms.controller.response

import com.koroliuk.emms.controller.request.PriceCategoryDto
import io.micronaut.core.annotation.Introspected


@Introspected
class GetPriceCategoryResponse(

    val page: Int,
    val size: Int,
    val totalSize: Long,
    val totalPage: Int,
    val content: List<PriceCategoryDto>

)
