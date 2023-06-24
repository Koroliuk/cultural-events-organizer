package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank


@Introspected
class GroupDto(

    @field:NotBlank
    val name: String

)
