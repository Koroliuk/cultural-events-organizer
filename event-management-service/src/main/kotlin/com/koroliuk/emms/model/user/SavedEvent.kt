package com.koroliuk.emms.model.user

import com.koroliuk.emms.model.event.Event
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "saved_events")
class SavedEvent(

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    val createdDate: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
