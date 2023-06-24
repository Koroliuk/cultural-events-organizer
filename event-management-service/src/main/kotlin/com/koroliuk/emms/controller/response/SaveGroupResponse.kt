package com.koroliuk.emms.controller.response

import io.micronaut.core.annotation.Introspected


@Introspected
class SaveGroupResponse(

    val id: Long,

    val groupId: Long

)
