package com.koroliuk.emms.repository.user

import com.koroliuk.emms.model.user.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?

}
