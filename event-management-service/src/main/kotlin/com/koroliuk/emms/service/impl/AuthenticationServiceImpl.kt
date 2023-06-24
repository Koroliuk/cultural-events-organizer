package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.request.SignUpRequest
import com.koroliuk.emms.model.user.USER
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.service.AuthenticationService
import com.koroliuk.emms.service.UserService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.commons.codec.digest.DigestUtils


@Singleton
class AuthenticationServiceImpl(
    @Inject private val userService: UserService,
) : AuthenticationService {

    override fun signUp(request: SignUpRequest) {
        val user = User(
            username = request.username,
            passwordHash = DigestUtils.sha256Hex(request.passwordHash),
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            role = USER
        )
        userService.create(user)
    }

}
