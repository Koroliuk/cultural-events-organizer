package com.ems.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import javax.validation.constraints.Positive


@Introspected
class PurchaseRequest(

    @NonNull
    @Positive
    val amount: Long,

    val isUnSubscribeFromWaitingList: Boolean,

    val discountCode: String? = null

)
