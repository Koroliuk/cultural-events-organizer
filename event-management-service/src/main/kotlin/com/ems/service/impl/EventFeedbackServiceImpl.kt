package com.ems.service.impl

import com.ems.model.EventFeedback
import com.ems.repository.EventFeedbackRepository
import com.ems.service.EventFeedbackService
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class EventFeedbackServiceImpl(
    @Inject private val eventFeedbackRepository: EventFeedbackRepository
) : EventFeedbackService {

    override fun leaveFeedback(eventFeedback: EventFeedback) {
        eventFeedbackRepository.save(eventFeedback)
    }

    override fun findByEventIdWithNonEmptyFeedback(id: Long): List<EventFeedback> {
        return eventFeedbackRepository.findByEventIdAndFeedbackIsNotNullAndFeedbackIsNotEmpty(id)
    }

    override fun countEventIdWithNonEmptyFeedback(id: Long): Long {
        return eventFeedbackRepository.countByEventIdAndFeedbackIsNotNullAndFeedbackIsNotEmpty(id)
    }

    override fun getAvgRateByEventId(id: Long): Float {
        return eventFeedbackRepository.getAvgRateByEventId(id)
    }

}
