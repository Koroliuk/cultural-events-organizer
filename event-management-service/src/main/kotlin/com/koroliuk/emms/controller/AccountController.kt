package com.koroliuk.emms.controller

import com.koroliuk.emms.controller.request.SignUpRequest
import com.koroliuk.emms.utils.ControllerUtils.createMessageResponse
import com.koroliuk.emms.service.AuthenticationService
import com.koroliuk.emms.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.util.*
import javax.validation.Valid


@Controller("/api/signup")
@Secured(SecurityRule.IS_ANONYMOUS)
class AccountController(
    @Inject private val authenticationService: AuthenticationService,
    @Inject private val userService: UserService
) {

    @Post
    fun signUp(@Valid @Body request: SignUpRequest): HttpResponse<Any> {
        if (userService.existByUsername(request.username)) {
            return HttpResponse.status<Any?>(HttpStatus.CONFLICT)
                .body(createMessageResponse("A user with that username already exists"))
        }
        authenticationService.signUp(request)
        return HttpResponse.created(createMessageResponse("You have successfully registered!"))
    }

}
