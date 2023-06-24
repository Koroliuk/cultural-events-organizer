package com.koroliuk.emms.controller.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import javax.validation.constraints.Positive


@Introspected
class PurchaseRequest(
    val seatId: Long?,
    val priceCategoryId: Long?,
    val discountCode: String?,
    val isUnSubscribeFromWaitingList: Boolean
)

