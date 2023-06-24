package com.koroliuk.emms.security

import com.koroliuk.emms.model.user.RefreshToken
import com.koroliuk.emms.repository.user.RefreshTokenRepository
import com.koroliuk.emms.service.UserService
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
import java.time.LocalDateTime


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
                    createdDate = LocalDateTime.now()
                )
                refreshTokenRepository.save(refreshToken)
            }
        }
    }

    override fun getAuthentication(refreshToken: String): Publisher<Authentication> {
        return Flux.create({ emitter: FluxSink<Authentication> ->
            val refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken)
            if (refreshTokenEntity != null) {
                if (refreshTokenEntity.createdDate.plusSeconds(30) < LocalDateTime.now()) {
                    refreshTokenRepository.delete(refreshTokenEntity)
                    emitter.error(
                        OauthErrorResponseException(
                            IssuingAnAccessTokenErrorCode.INVALID_GRANT,
                            "Refresh token is out of date and will be deleted",
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
                        "Refresh token not found",
                        null
                    )
                )
            }
        }, FluxSink.OverflowStrategy.ERROR)
    }

}
