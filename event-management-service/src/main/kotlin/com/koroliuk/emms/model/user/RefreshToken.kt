package com.koroliuk.emms.model.user

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(

    @OneToOne(fetch = FetchType.EAGER)
    var user: User?,

    val token: String,

    @Column(name = "created_date")
    val createdDate: LocalDateTime,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
