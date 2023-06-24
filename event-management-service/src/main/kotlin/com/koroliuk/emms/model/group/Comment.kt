package com.koroliuk.emms.model.group

import com.koroliuk.emms.model.user.User
import javax.persistence.*


@Entity
@Table(name = "comments")
class Comment(

    @ManyToOne
    val author: User,

    @ManyToOne
    val group: Group,

    val text: String,

    var replyTo: Long? = null,

    var isHidden: Boolean = false,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
