package com.koroliuk.emms.model.event

import javax.persistence.*


@Entity
@Table(name = "event_categories")
class EventCategory(

    @Column(unique = true)
    val name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
