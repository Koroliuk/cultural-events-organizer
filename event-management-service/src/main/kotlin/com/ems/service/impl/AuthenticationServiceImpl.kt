package com.ems.service.impl

import com.ems.model.Role
import com.ems.model.User
import com.ems.security.CustomPasswordEncoder
import com.ems.service.AuthenticationService
import com.ems.service.UserService
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
