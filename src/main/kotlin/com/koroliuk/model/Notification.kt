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

    @Column(nullable = false)
    val created: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
