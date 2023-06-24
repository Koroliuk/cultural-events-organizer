package com.koroliuk.emms.security

import com.koroliuk.emms.service.UserService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.commons.codec.digest.DigestUtils
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink


@Singleton
class AuthenticationProviderUserPassword(
    @Inject private val userService: UserService,
) : AuthenticationProvider {

    override fun authenticate(httpRequest: HttpRequest<*>?,
                              authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        val username = authenticationRequest.identity as String
        val password = authenticationRequest.secret as String
        val user = userService.findByUsername(username)
        return Flux.create({ emitter: FluxSink<AuthenticationResponse> ->
            if (user != null && user.username == username && DigestUtils.sha256Hex(password) == user.passwordHash) {
                emitter.next(AuthenticationResponse.success(username, listOf(user.role)))
                emitter.complete()
            } else {
                emitter.error(AuthenticationResponse.exception())
            }
        }, FluxSink.OverflowStrategy.ERROR)
    }
}
