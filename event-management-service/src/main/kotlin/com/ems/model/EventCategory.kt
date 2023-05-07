package com.ems.model

import javax.persistence.*


@Entity
@Table(name = "event_categories")
class EventCategory (

        @Column(unique = true, nullable = false)
        val name: String,

        val description: String? = null,

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null

)

