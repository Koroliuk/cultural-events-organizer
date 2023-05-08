package com.ems.model

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val eventType: EventType,

    @ManyToOne
    val category: EventCategory,

    @OneToOne
    val creator: User,

    @Column
    val location: String?,

    @Column
    val url: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
