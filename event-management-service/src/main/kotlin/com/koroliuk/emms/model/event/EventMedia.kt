package com.koroliuk.emms.model.event

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*


@Entity
@Table(name = "event_media")
class EventMedia(

    @Column(unique = true)
    val key: String,

    @ManyToOne
    @JsonBackReference
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
