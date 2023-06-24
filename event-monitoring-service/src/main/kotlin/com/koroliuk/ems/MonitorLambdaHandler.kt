package com.koroliuk.ems

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.koroliuk.ems.model.Notification
import com.koroliuk.ems.repository.EventRepository
import com.koroliuk.ems.repository.NotificationRepository
import com.koroliuk.ncs.config.MonitorCleanupServiceProperties
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import java.time.LocalDateTime
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class MonitorLambdaHandler: MicronautRequestHandler<ScheduledEvent?, Void?>() {

    @Inject
    lateinit var eventRepository: EventRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var configurationProperties: MonitorCleanupServiceProperties

    override fun execute(input: ScheduledEvent?): Void? {
        val batchSize = configurationProperties.batchSize
        if (batchSize == null) {
            logger.info("No batch size specified")
            return null
        }
        logger.info("Batch size $batchSize")
        runBlocking {
            val job1 = launch {
                handleEndedEvents(batchSize)
            }
            val job2 = launch {
                handleStartingEvents(batchSize)
            }

            job1.join()
            job2.join()
        }

        return null
    }

    private fun handleEndedEvents(batchSize: Int) {
        val endedEvents = eventRepository.findEndedEvents(batchSize)
        endedEvents.forEach {
            val rateNotification = Notification(
                message = "Rate this event",
                userId = it.userId,
                eventId = it.eventId,
                groupId = it.groupId,
                type = "EVENT_RATE",
                created = LocalDateTime.now()
            )
            notificationRepository.save(rateNotification)
        }
    }

    private fun handleStartingEvents(batchSize: Int) {
        val startingEvents = eventRepository.findStartingEvents(batchSize)
        startingEvents.forEach {
            val startNotification = Notification(
                message = "Your event is starting soon",
                userId = it.userId,
                eventId = it.eventId,
                groupId = it.groupId,
                type = "EVENT_START",
                created = LocalDateTime.now()
            )
            notificationRepository.save(startNotification)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MonitorLambdaHandler::class.java)
    }

}
