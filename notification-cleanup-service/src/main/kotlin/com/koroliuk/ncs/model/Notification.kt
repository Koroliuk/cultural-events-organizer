package com.koroliuk.ncs.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "notifications")
class Notification (

    val message: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "event_id", nullable = false)
    val eventId: Long? = null,

    @Column(name = "group_id", nullable = false)
    val groupId: Long? = null,

    @Column(nullable = false)
    var type: String? = null,

    val created: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
