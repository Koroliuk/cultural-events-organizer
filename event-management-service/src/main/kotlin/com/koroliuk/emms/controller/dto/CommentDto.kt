package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected


@Introspected
class CommentDto(

    val id: Long,
    val text: String,
    val username: String,
    val groupId: Long
)