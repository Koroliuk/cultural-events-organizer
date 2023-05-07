package com.ems.service.impl

import com.ems.repository.UserRepository
import com.ems.service.UserService
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class UserServiceImpl (
    @Inject private val userRepository: UserRepository
) : UserService {

    override fun create(user: com.ems.model.User) {
        val username = user.username
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("User with email $username already exists")
        }
        userRepository.save(user)
    }

    override fun update(user: com.ems.model.User) {
        userRepository.update(user)
    }

    override fun findByUsername(username: String): com.ems.model.User? {
        return userRepository.findByUsername(username)
    }

}
