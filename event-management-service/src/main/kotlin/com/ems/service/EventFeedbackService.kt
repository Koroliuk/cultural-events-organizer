package com.ems.service

import com.ems.model.EventFeedback

interface EventFeedbackService {

    fun leaveFeedback(eventFeedback: EventFeedback)

    fun findByEventIdWithNonEmptyFeedback(id: Long): List<EventFeedback>

}
