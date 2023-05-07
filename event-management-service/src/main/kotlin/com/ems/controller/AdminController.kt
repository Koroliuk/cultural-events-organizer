package com.ems.controller

import com.ems.dto.BlockDto
import com.ems.model.Role
import com.ems.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import io.micronaut.security.annotation.Secured
import jakarta.inject.Inject

@Controller("/api/users")
@Secured("ADMIN")
class AdminController (
    @Inject private val userService: UserService
) {

    @Put("/block")
    fun blockUser(@Body blockDto: BlockDto): HttpResponse<Any> {
        val user = userService.findByUsername(blockDto.username)
        if (user != null) {
            if (Role.ADMIN == user.role) {
                return HttpResponse.badRequest("Can not block admin user")
            }
            user.blocked = true
            userService.update(user)
            return HttpResponse.ok()
        }
        return HttpResponse.notFound("User with such a username not found")
    }

    @Put("/unblock")
    fun unblockUser(@Body blockDto: BlockDto): HttpResponse<Any> {
        val user = userService.findByUsername(blockDto.username)
        if (user != null) {
            if (Role.ADMIN == user.role) {
                return HttpResponse.badRequest("Can not unblock admin user")
            }
            user.blocked = false
            userService.update(user)
            return HttpResponse.ok()
        }
        return HttpResponse.notFound("User with such a username not found")
    }

}
