package com.koroliuk.security

import com.koroliuk.model.RefreshToken
import com.koroliuk.repository.RefreshTokenRepository
import com.koroliuk.service.UserService
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.errors.IssuingAnAccessTokenErrorCode
import io.micronaut.security.errors.OauthErrorResponseException
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent
import io.micronaut.security.token.refresh.RefreshTokenPersistence
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.time.Instant


@Singleton
class CustomRefreshTokenPersistence(
    @Inject private val refreshTokenRepository: RefreshTokenRepository,
    @Inject private val userService: UserService
) : RefreshTokenPersistence {

    override fun persistToken(event: RefreshTokenGeneratedEvent) {
        if (event.refreshToken != null && event.authentication != null && event.authentication.name != null) {
            val username = event.authentication.name
            val user = userService.findByUsername(username)
            if (user != null) {
                val refreshToken = RefreshToken(
                    user = user,
                    token = event.refreshToken,
                    revoked = false,
                    dateCreated = Instant.now()
                )
                refreshTokenRepository.save(refreshToken)
            }
        }
    }

    override fun getAuthentication(refreshToken: String): Publisher<Authentication> {
        return Flux.create({ emitter: FluxSink<Authentication> ->
            val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
            if (refreshTokenEntity != null) {
                if (refreshTokenEntity.revoked) {
                    emitter.error(
                        OauthErrorResponseException(
                            IssuingAnAccessTokenErrorCode.INVALID_GRANT,
                            "refresh token revoked",
                            null
                        )
                    )
                } else {
                    val user = refreshTokenEntity.user
                    emitter.next(Authentication.build(user!!.username))
                    emitter.complete()
                }
            } else {
                emitter.error(
                    OauthErrorResponseException(
                        IssuingAnAccessTokenErrorCode.INVALID_GRANT,
                        "refresh token not found",
                        null
                    )
                )
            }
        }, FluxSink.OverflowStrategy.ERROR)
    }
}
