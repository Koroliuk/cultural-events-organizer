package com.koroliuk.emms.model.event

import com.koroliuk.emms.model.user.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "event_feedbacks")
class EventFeedback(

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    val rate: Int,

    val text: String? = null,

    val created: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
