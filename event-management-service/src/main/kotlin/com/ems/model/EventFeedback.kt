package com.ems.model

import javax.persistence.*

@Entity
@Table(name = "event_feedbacks")
class EventFeedback (

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    @Column(nullable = false)
    val rate: Int,

    val feedback: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
