package com.koroliuk.emms.controller.response

import io.micronaut.core.annotation.Introspected


@Introspected
class GroupSubscriptionResponse(

    val id: Long,

    val groupId: Long,

    val username: String? = null

)
