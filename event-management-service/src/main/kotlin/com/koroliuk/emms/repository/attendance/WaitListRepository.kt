package com.koroliuk.emms.repository.attendance

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.attendance.WaitList
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface WaitListRepository : CrudRepository<WaitList, Long> {

    fun deleteByEventAndUser(event: Event, user: User)

    fun findByEvent(event: Event): List<WaitList>

}