package com.koroliuk.model

import java.time.LocalDateTime

data class Event(
    val id: Long,
    val name: String,
    val start: LocalDateTime,
    val end: LocalDateTime
)
