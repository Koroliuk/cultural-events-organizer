package com.ems.service

import com.ems.model.User


interface UserService {

    fun create(user: User)

    fun update(user: User)

    fun findByUsername(username: String): User?

}
