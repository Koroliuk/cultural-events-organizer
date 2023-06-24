package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.EventFeedbackResponse
import com.koroliuk.emms.controller.response.GetEventFeedbacksResponse
import com.koroliuk.emms.model.event.Event
import com.koroliuk.emms.model.event.EventFeedback
import com.koroliuk.emms.model.user.User
import com.koroliuk.emms.repository.event.EventFeedbackRepository
import com.koroliuk.emms.service.EventFeedbackService
import com.koroliuk.emms.utils.ControllerUtils.formatter
import io.micronaut.data.model.Pageable
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
class EventFeedbackServiceImpl(
    @Inject private val eventFeedbackRepository: EventFeedbackRepository
) : EventFeedbackService {

    override fun leaveFeedback(event: Event, rate: Int, text: String?, user: User): EventFeedbackResponse {
        val eventFeedback = EventFeedback(
                event = event,
                rate = rate,
                text = text,
                created = LocalDateTime.now(),
                user = user
        )
        return convertToDto(eventFeedbackRepository.save(eventFeedback))
    }

    override fun findById(id: Long): EventFeedbackResponse? {
        return eventFeedbackRepository.findById(id)
                .map { convertToDto(it) }
                .orElse(null)
    }

    override fun findByEventId(id: Long, page: Int, size: Int): GetEventFeedbacksResponse {
        val pageable = Pageable.from(page, size)
        val result = eventFeedbackRepository.findByEventIdOrderById(id, pageable)
        return GetEventFeedbacksResponse(
            page = result.pageable.number,
            size = result.pageable.size,
            totalSize = result.totalSize,
            totalPage = result.totalPages,
            content = result.content.stream()
                .map { convertToDto(it) }
                .toList()
        )
    }

    override fun isUserAlreadyLeftFeedback(user: User, event: Event): Boolean {
        return eventFeedbackRepository.existsByUserAndEvent(user, event)
    }


    override fun countEventIdWithNonEmptyFeedback(id: Long): Long {
        return eventFeedbackRepository.countByEventIdAndTextIsNotNullAndTextIsNotEmpty(id)
    }

    override fun getAvgRateByEventId(id: Long): Float {
        return eventFeedbackRepository.getAvgRateByEventId(id)
    }

    private fun convertToDto(eventFeedback: EventFeedback): EventFeedbackResponse {
        return EventFeedbackResponse(
            eventId = eventFeedback.event.id!!,
            rate = eventFeedback.rate,
            createdDate = formatter.format(eventFeedback.created),
            feedback = eventFeedback.text
        )
    }

}
