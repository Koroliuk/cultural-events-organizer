package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero


@Introspected
class PriceCategoryUpdateDto (

    var name: String?,

    @field:PositiveOrZero
    var price: BigDecimal?

)
