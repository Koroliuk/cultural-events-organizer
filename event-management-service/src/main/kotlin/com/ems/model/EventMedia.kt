package com.ems.model

import javax.persistence.*

@Entity
@Table
class EventMedia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val s3Key: String,

    @ManyToOne
    val event: Event
)
