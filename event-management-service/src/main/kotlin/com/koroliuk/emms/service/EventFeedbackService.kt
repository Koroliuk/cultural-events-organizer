package com.koroliuk.emms.service

import com.koroliuk.emms.controller.response.EventFeedbackResponse
import com.koroliuk.emms.controller.response.GetEventFeedbacksResponse
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.user.User

interface EventFeedbackService {

    fun leaveFeedback(event: Event, rate: Int, text: String?, user: User): EventFeedbackResponse

    fun findById(id: Long): EventFeedbackResponse?

    fun findByEventId(id: Long, page: Int, size: Int): GetEventFeedbacksResponse

    fun isUserAlreadyLeftFeedback(user: User, event: Event): Boolean

    fun countEventIdWithNonEmptyFeedback(id: Long): Long

    fun getAvgRateByEventId(id: Long): Float


}
