package com.koroliuk.emms.model.user

import com.koroliuk.emms.model.event.Event
import javax.persistence.*

@Entity
@Table(name = "volunteers")
class VolunteerApplication(

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    @Enumerated(EnumType.STRING)
    var status: VolunteerApplicationStatus = VolunteerApplicationStatus.PENDING,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
