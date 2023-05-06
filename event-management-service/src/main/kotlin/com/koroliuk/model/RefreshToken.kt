package com.koroliuk.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(

    @OneToOne(fetch = FetchType.EAGER)
    var user: User?,

    val token: String,

    val revoked: Boolean,

    @Column(name = "date_created", nullable = false)
    val dateCreated: Instant,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
