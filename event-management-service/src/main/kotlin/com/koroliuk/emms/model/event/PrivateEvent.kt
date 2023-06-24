package com.koroliuk.emms.model.event

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*


@Entity
@Table(name = "private_events")
class PrivateEvent(

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id")
    @JsonBackReference
    val event: Event,

    var invitationCode: String,

    @Id
    @Column(name = "event_id")
    val id: Long? = null

)
