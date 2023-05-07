package com.ems.service

import com.ems.model.User


interface AuthenticationService {

    fun signUp(user: User)

}
