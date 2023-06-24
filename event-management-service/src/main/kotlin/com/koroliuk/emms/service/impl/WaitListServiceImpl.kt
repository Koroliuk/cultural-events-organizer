package com.koroliuk.emms.service.impl

import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.model.attendance.WaitList
import com.koroliuk.emms.repository.attendance.WaitListRepository
import com.koroliuk.emms.service.WaitListService
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
