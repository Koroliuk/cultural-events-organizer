package com.ems.model

import javax.persistence.*


@Entity
@Table(name = "wait_list")
class WaitList(

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
