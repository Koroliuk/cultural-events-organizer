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

    @Column(nullable = false)
    val isTicketsLimited: Boolean,

    @Column(name = "max_tickets")
    val maxTickets: Long? = null,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    var isBlocked: Boolean = false,

    @Column
    val url: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
