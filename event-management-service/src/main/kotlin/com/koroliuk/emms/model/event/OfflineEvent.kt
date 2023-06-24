package com.koroliuk.emms.model.event

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*


@Entity
@Table(name = "offline_events")
class OfflineEvent(

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id")
    @JsonBackReference
    val event: Event,

    var location: String,

    @Id
    @Column(name = "event_id")
    val id: Long? = null,

)
