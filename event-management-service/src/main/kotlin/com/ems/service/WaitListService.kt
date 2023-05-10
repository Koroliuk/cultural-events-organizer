package com.ems.service

import com.ems.model.Event
import com.ems.model.User
import com.ems.model.WaitList

interface WaitListService {

    fun create(event: Event, user: User): WaitList

    fun deleteByEventAndUser(event: Event, user: User)

    fun findByEvent(event: Event): List<WaitList>

}