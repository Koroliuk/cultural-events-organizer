package com.koroliuk.emms.service

import com.koroliuk.emms.controller.request.SignUpRequest


interface AuthenticationService {

    fun signUp(request: SignUpRequest)

}
