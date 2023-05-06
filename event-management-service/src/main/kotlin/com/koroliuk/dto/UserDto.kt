package com.koroliuk.dto

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


@Introspected
data class UserDto(
    @NotBlank
    val username: String,

    @NotBlank
    val passwordHash: String,

    @NotBlank
    @Email
    val email: String,

    @NotBlank
    val firstName: String,

    @NotBlank
    val lastName: String,
)
