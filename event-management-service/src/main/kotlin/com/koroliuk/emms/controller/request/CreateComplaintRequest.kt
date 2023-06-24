package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
class CreateComplaintRequest(

    @field:NotBlank
    val reason: String,

    @field:NotNull
    val objectId: Long,

    @field:NotBlank
    val type: String

)
