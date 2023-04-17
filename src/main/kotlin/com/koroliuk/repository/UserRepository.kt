package com.koroliuk.repository

import com.koroliuk.model.User
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface UserRepository : CrudRepository<User, Long> {

    fun existsByUsername(username: String): Boolean

}
