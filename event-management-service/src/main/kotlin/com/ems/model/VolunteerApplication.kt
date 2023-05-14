package com.ems.model

import javax.persistence.*

@Entity
@Table(name = "volunteer_applications")
class VolunteerApplication (
    @ManyToOne
    val user: User,

    @ManyToOne
    val event: Event,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: VolunteerApplicationStatus = VolunteerApplicationStatus.PENDING,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

enum class VolunteerApplicationStatus {
    PENDING,
    APPROVED,
    REJECTED
}
