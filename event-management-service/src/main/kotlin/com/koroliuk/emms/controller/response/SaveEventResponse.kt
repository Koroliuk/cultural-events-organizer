package com.koroliuk.emms.controller.response

import io.micronaut.core.annotation.Introspected


@Introspected
class SaveEventResponse(

    val id: Long,

    val eventId: Long

)
