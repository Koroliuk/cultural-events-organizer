package com.koroliuk.service.impl

import com.koroliuk.model.User
import com.koroliuk.repository.UserRepository
import com.koroliuk.service.UserService
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class UserServiceImpl (
    @Inject private val userRepository: UserRepository
) : UserService {

    override fun create(user: User) {
        val username = user.username
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("User with email $username already exists")
        }
        userRepository.save(user)
    }

    override fun update(user: User) {
        userRepository.update(user)
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

}
