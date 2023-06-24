package com.koroliuk.emms.controller.response

import io.micronaut.core.annotation.Introspected


@Introspected
class GetGroupSubscriptionsResponse(

    val page: Int,

    val size: Int,

    val totalSize: Long,

    val totalPage: Int,

    val content: List<GroupSubscriptionResponse>

)