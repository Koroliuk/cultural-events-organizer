package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue


@Introspected
class EventRequest {

    @PathVariable
    var id: Long? = null

}
