package com.ems.model

import javax.persistence.*

@Entity
@Table(name = "discount_codes")
class DiscountCode(
    @Column(nullable = false)
    val code: String,

    @Column(nullable = false)
    val discountPercentage: Float,

    @ManyToOne
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)
