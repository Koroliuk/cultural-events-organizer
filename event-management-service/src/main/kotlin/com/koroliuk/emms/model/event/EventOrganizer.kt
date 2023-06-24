package com.koroliuk.emms.model.event

import com.koroliuk.emms.model.group.Group
import javax.persistence.*


@Entity
@Table(name = "event_organizers")
class EventOrganizer(

    @ManyToOne
    val group: Group,

    @ManyToOne
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
