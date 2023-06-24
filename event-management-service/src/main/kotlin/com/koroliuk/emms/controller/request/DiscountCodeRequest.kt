package com.koroliuk.emms.controller.request

import com.koroliuk.emms.model.attendance.DiscountType
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.convert.format.Format
import java.time.LocalDateTime
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank


@Introspected
class DiscountCodeRequest(

    @NotBlank
    val code: String,

    @NonNull
    val type: DiscountType,

    @NonNull
    val value: Long,

    @Format("yyyy-MM-dd'T'HH:mm:ss'Z'")
    @NonNull
    val expiredDate: LocalDateTime

)
