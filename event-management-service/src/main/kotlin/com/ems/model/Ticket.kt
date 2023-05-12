package com.ems.model

import java.time.Instant
import javax.persistence.*


@Entity
@Table(name = "tickets")
class Ticket(

    @ManyToOne
    val event: Event,

    @ManyToOne
    val user: User,

    @Column(name = "purchase_time", nullable = false)
    val purchaseTime: Instant,

    @Column(nullable = false)
    val price: Double,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TicketStatus,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
