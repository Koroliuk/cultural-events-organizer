package com.koroliuk.emms.model.user

import javax.persistence.*


@Entity
@Table(name = "users")
class User(
    @Column(unique = true)
    val username: String,

    @Column(name = "password_hash")
    var passwordHash: String,

    val email: String,

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    var role: String,

    var isBlocked: Boolean = false,

    var isSubscribedFor: Boolean = true,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)
