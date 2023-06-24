package com.koroliuk.emms.service

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.attendance.WaitList

interface WaitListService {

    fun create(event: Event, user: User): WaitList

    fun deleteByEventAndUser(event: Event, user: User)

    fun findByEvent(event: Event): List<WaitList>

}