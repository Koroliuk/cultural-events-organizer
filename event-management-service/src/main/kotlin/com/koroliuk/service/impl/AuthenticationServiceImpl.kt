package com.koroliuk.service.impl

import com.koroliuk.model.Role
import com.koroliuk.model.User
import com.koroliuk.security.CustomPasswordEncoder
import com.koroliuk.service.AuthenticationService
import com.koroliuk.service.UserService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AuthenticationServiceImpl(
    @Inject private val userService: UserService,
    @Inject private val customPasswordEncoder: CustomPasswordEncoder
) : AuthenticationService {

    override fun signUp(user: User) {
        user.passwordHash = customPasswordEncoder.encode(user.passwordHash)
        user.role = Role.USER
        userService.create(user)
    }

}
