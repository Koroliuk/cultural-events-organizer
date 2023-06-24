package com.koroliuk.emms.model.event

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Entity
@Table(name = "event_volunteers")
class EventVolunteers(

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id")
    @JsonBackReference
    val event: Event,

    var amount: Int,

    @Id
    @Column(name = "event_id")
    val id: Long? = null

)