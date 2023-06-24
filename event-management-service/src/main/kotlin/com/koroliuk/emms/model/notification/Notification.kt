package com.koroliuk.emms.model.notification

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.group.Group
import com.koroliuk.emms.model.user.User
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "notifications")
class Notification(

    val message: String,

    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event? = null,

    @ManyToOne
    val group: Group? = null,

    @Enumerated(EnumType.STRING)
    var type: NotificationType,

    val created: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
