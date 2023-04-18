package com.koroliuk.security

import com.koroliuk.service.UserService
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink


@Singleton
class AuthenticationProviderUserPassword(
    @Inject private val userService: UserService,
    @Inject private val customPasswordEncoder: CustomPasswordEncoder
) : AuthenticationProvider {

    override fun authenticate(httpRequest: HttpRequest<*>?,
                              authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        val username = authenticationRequest.identity as String
        val password = authenticationRequest.secret as String
        val user = userService.findByUsername(username)
        return Flux.create({ emitter: FluxSink<AuthenticationResponse> ->
            if (user != null && user.username == username && customPasswordEncoder.matches(password, user.passwordHash)) {
                emitter.next(AuthenticationResponse.success(username))
                emitter.complete()
            } else {
                emitter.error(AuthenticationResponse.exception())
            }
        }, FluxSink.OverflowStrategy.ERROR)
    }
}
