package com.koroliuk.emms.model.attendance

import com.koroliuk.emms.model.event.Event
import javax.persistence.*


@Entity
@Table
class Seat(

    var number: Int,

    @ManyToOne
    val event: Event,

    @ManyToOne
    var priceCategory: PriceCategory,

    var isTaken: Boolean = false,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
