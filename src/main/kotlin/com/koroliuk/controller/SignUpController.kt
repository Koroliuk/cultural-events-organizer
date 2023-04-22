package com.koroliuk.controller

import com.koroliuk.dto.UserDto
import com.koroliuk.model.Role
import com.koroliuk.service.AuthenticationService
import com.koroliuk.utils.MappingUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.util.*


@Controller("/api/signup")
@Secured(SecurityRule.IS_ANONYMOUS)
class SignUpController (@Inject private val authenticationService: AuthenticationService) {

    @Post
    fun signUp(@Body userDto: UserDto): HttpResponse<Any> {
        val user = MappingUtils.convertToEntity(userDto)
        user.role = Role.USER
        authenticationService.signUp(user)
        return HttpResponse.created("Successfully signed up!")
    }

}
