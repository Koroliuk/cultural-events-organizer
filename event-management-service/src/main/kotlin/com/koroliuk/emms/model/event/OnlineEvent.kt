package com.koroliuk.emms.model.event

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*


@Entity
@Table(name = "online_events")
class OnlineEvent(

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id")
    @JsonBackReference
    val event: Event,

    var url: String,

    @Id
    @Column(name = "event_id")
    val id: Long? = null

)
