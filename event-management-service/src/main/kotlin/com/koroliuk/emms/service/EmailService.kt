package com.koroliuk.emms.service

import com.koroliuk.emms.model.user.User

interface EmailService {

    fun sendMessageToQueue(text: String, sendTo: String)

}
