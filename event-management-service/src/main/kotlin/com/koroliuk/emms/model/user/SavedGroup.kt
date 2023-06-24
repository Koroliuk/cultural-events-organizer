package com.koroliuk.emms.model.user

import com.koroliuk.emms.model.group.Group
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "saved_groups")
class SavedGroup(

    @ManyToOne
    val user: User,

    @ManyToOne
    val group: Group,

    val createdDate: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

)
