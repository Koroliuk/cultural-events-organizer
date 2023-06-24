package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Introspected
class EventCreateRequest(

    @field:Max(value = 10)
    @field:Min(value = 0)
    @field:NonNull
    val rate: Int,

    val feedback: String? = null,

)
