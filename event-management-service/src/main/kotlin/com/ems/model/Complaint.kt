package com.ems.model

import javax.persistence.*

@Entity
class Complaint(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ComplaintStatus,

    @ManyToOne
    val event: Event,

    @ManyToOne
    val author: User,

    val text: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
