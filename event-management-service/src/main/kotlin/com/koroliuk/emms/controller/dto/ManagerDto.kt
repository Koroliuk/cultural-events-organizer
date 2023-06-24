package com.koroliuk.emms.controller.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank


@Introspected
class ManagerDto(

    @field:NotBlank
    var username: String,

)