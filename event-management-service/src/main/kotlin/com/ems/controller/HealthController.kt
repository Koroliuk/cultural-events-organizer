package com.ems.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.util.*


@Controller("/health")
@Secured(SecurityRule.IS_ANONYMOUS)
class HealthController() {

    @Get
    fun signUp(): HttpResponse<Any> {
        return HttpResponse.ok()
    }

}
