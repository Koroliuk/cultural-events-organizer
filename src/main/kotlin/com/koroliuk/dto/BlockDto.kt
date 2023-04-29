package com.koroliuk.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class BlockDto(

    @NotBlank
    val username: String

)
