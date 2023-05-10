package com.ems.service.impl

import com.ems.model.Event
import com.ems.model.User
import com.ems.model.WaitList
import com.ems.repository.WaitListRepository
import com.ems.service.WaitListService
import jakarta.inject.Inject
import jakarta.inject.Singleton


@Singleton
class WaitListServiceImpl(
    @Inject private val waitListRepository: WaitListRepository
) : WaitListService {

    override fun create(event: Event, user: User): WaitList {
        val entity = WaitList(
            event = event,
            user = user
        )
        return waitListRepository.save(entity)
    }

    override fun deleteByEventAndUser(event: Event, user: User) {
        waitListRepository.deleteByEventAndUser(event, user)
    }

    override fun findByEvent(event: Event): List<WaitList> {
        return waitListRepository.findByEvent(event)
    }
}
