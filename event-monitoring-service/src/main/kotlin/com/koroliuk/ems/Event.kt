package com.koroliuk.ems

import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "events")
class Event(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val startTime: LocalDateTime,

    @Column(nullable = false)
    val endTime: LocalDateTime,

    @Column(nullable = false)
    val eventType: String,

    @Column
    val location: String?,

    @Column
    val url: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
