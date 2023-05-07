package com.ems.repository

import com.ems.model.RefreshToken
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {

    fun findByToken(token: String): RefreshToken?

}
