package com.ems.repository

import com.ems.model.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?

}
