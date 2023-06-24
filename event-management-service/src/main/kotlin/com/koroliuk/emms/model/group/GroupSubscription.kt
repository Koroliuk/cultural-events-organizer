package com.koroliuk.emms.model.group

import com.koroliuk.emms.model.user.User
import javax.persistence.*


@Entity
@Table(name = "group_subscriptions")
class GroupSubscription(

    @ManyToOne
    val user: User,

    @ManyToOne
    val group: Group,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
