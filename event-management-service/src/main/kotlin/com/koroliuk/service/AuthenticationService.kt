package com.koroliuk.service

import com.koroliuk.model.User


interface AuthenticationService {

    fun signUp(user: User)

}
