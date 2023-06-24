package com.koroliuk.emms.model.attendance

import com.koroliuk.emms.model.event.Event
import java.math.BigDecimal
import javax.persistence.*


@Entity
@Table(name = "price_categories")
class PriceCategory(

    var name: String,

    var price: BigDecimal,

    @ManyToOne
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

)
