package com.koroliuk.emms.controller.dto

import com.koroliuk.emms.model.group.GroupManagerRole
import io.micronaut.core.annotation.Introspected

@Introspected
class GroupManagerInfo(

    val id: Long,
    val name: String,
    val role: GroupManagerRole

)