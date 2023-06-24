package com.koroliuk.ems.model

import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "events")
class Event(

    val name: String,

    val description: String,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,

    @Column(name = "category_id", nullable = false)
    val categoryId: Long,

    @Column(name = "is_blocked", nullable = false)
    val isBlocked: Long,

    @Column(name = "visibility_type", nullable = false)
    val visibilityType: Long,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
