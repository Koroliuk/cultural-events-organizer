package com.koroliuk.emms.repository.user

import com.koroliuk.emms.model.user.RefreshToken
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository


@Repository
interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {

    fun findByToken(token: String): RefreshToken?

}
