package com.koroliuk.emms.model.attendance

import com.koroliuk.emms.model.event.Event
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "discount_codes")
class DiscountCode(

    val code: String,

    @Column(unique = true)
    val value: Long,

    @Enumerated(EnumType.STRING)
    val type: DiscountType,

    val expirationDate: LocalDateTime,

    @ManyToOne
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
