package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank


@Introspected
class CommentCreateDto(

    @field:NotBlank
    val text: String,

    val replyTo: Long? = null
)
