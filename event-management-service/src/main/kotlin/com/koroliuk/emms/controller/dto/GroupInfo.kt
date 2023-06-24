package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected


@Introspected
class GroupInfo(

    val managers: List<GroupManagerInfo>,
    val isBlocked: Boolean,
    val name: String,
    val id: Long

)
