package com.koroliuk.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "notifications")
class Notification (

    @Column(nullable = false)
    val message: String,

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: NotificationType? = null,


    @Column(nullable = false)
    val created: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
