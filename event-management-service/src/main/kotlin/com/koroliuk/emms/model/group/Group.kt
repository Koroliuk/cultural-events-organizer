package com.koroliuk.emms.model.group

import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*


@Entity
@Table(name = "groups")
class Group(

    @field:Column(unique = true)
    var name: String,

    @field:Column(unique = true)
    var isBlocked: Boolean = false,

    @field:OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @field:JoinColumn(name = "group_id")
    @field:JsonManagedReference
    val managers: MutableList<GroupManager>,

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
