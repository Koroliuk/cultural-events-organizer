package com.koroliuk.emms.model.complaint

import com.koroliuk.emms.model.event.Event
import javax.persistence.*

@Entity
@Table(name = "event_complaints")
class EventComplaint(

    @OneToOne
    @MapsId
    @JoinColumn(name = "complaint_id")
    val complaint: Complaint,

    @ManyToOne
    val event: Event,

    @Id
    @Column(name = "complaint_id")
    var id: Long? = null

)
