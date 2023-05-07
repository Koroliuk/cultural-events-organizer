package com.koroliuk.ems

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import java.time.LocalDateTime

class FunctionRequestHandler: MicronautRequestHandler<ScheduledEvent?, Void?>() {

    @Inject
    lateinit var eventRepository: EventRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun execute(input: ScheduledEvent?): Void? {
        eventRepository.findEndedEvents()
            .forEach { (eventId, userId) ->
                val rateNotification = Notification(
                    message = "Rate this event",
                    userId = userId,
                    eventId = eventId,
                    type = "EVENT_RATE",
                    created = LocalDateTime.now()
                )
                notificationRepository.save(rateNotification)
            }
        return null
    }

}
