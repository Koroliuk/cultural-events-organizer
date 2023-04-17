package com.koroliuk.service.impl

import com.koroliuk.model.User
import com.koroliuk.service.AuthenticationService
import com.koroliuk.service.UserService
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class AuthenticationServiceImpl(
    @Inject private val userService: UserService
) : AuthenticationService {

    override fun signUp(user: User) {
        //TODO: add password encryption
        userService.create(user)
    }

}
