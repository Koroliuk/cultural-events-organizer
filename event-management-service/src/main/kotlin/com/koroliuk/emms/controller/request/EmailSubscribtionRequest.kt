package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Introspected
class EmailSubscribtionRequest(

    val isSubscribed: Boolean

)
