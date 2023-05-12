package com.ems.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank


@Introspected
class DiscountCodeRequest(

    @NonNull
    val eventId: Long,

    @NotBlank
    val code: String,

    @NonNull
    @Min(value = 1)
    @Max(value = 100)
    val discountPercentage: Float
)
