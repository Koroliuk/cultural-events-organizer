package com.ems.repository

import com.ems.model.Event
import com.ems.model.User
import com.ems.model.WaitList
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface WaitListRepository : CrudRepository<WaitList, Long> {

    fun deleteByEventAndUser(event: Event, user: User)

    fun findByEvent(event: Event): List<WaitList>

}