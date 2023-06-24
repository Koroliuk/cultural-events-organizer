package com.koroliuk.emms.model.group

import com.fasterxml.jackson.annotation.JsonBackReference
import com.koroliuk.emms.model.user.User
import javax.persistence.*


@Entity
@Table(name = "group_managers")
class GroupManager(

    @ManyToOne
    @JsonBackReference
    val group: Group,

    @ManyToOne
    val user: User,

    @Enumerated(EnumType.STRING)
    val role: GroupManagerRole,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
