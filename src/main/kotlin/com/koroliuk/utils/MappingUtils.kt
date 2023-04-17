package com.koroliuk.utils

import com.koroliuk.dto.UserDto
import com.koroliuk.model.User

object MappingUtils {

    fun convertToEntity(userDto: UserDto): User {
        return User(
            username = userDto.username,
            passwordHash = userDto.passwordHash,
            email = userDto.email,
            firstName = userDto.firstName,
            lastName = userDto.lastName
        )
    }

}