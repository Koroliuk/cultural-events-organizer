package com.koroliuk.emms.model.attendance

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import javax.persistence.*


@Entity
@Table(name = "wait_lists")
class WaitList(

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
