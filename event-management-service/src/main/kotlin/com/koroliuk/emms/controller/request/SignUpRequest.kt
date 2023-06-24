package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


@Introspected
data class SignUpRequest(

    @field:NotBlank
    @field:Size(min = 5, max = 20)
    @field:Pattern(regexp = "^(?!.*\\.\\.)(?!.*\\.$)[^\\W][\\w.]{4,20}$")
    val username: String,

    @field:NotBlank
    @field:Size(min = 8, max = 30)
    @field:Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,30}$")
    val passwordHash: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 5, max = 50)
    val firstName: String,

    @field:NotBlank
    @field:Size(min = 5, max = 50)
    val lastName: String

)
