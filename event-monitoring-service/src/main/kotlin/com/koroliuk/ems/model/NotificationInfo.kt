package com.koroliuk.ems.model

import io.micronaut.core.annotation.Introspected

@Introspected
class NotificationInfo(

    val userId: Long,
    val groupId: Long,
    val eventId: Long

)