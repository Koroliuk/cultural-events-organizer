package com.koroliuk.emms.controller.response

import io.micronaut.core.annotation.Introspected


@Introspected
class CommentResponse(

    val id: Long,
    val text: String,
    val username: String,
    val groupId: Long,
    var replyTo: Long? = null,
    )