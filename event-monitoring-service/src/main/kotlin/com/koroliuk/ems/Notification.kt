package com.koroliuk.ems

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "notifications")
class Notification (

    @Column(nullable = false)
    val message: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "event_id", nullable = false)
    val eventId: Long? = null,

    @Column(nullable = false)
    var type: String? = null,

    @Column(nullable = false)
    val created: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
