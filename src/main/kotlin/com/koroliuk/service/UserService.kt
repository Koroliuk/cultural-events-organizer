package com.koroliuk.service

import com.koroliuk.model.User


interface UserService {

    fun create(user: User)

    fun findByUsername(username: String): User?

}
