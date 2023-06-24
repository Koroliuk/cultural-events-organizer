package com.koroliuk.ncs

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.koroliuk.ncs.config.NotificationCleanupServiceProperties
import com.koroliuk.ncs.repository.NotificationRepository
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


class CleanupLambdaHandler: MicronautRequestHandler<ScheduledEvent?, Void?>() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var configurationProperties: NotificationCleanupServiceProperties

    override fun execute(input: ScheduledEvent?): Void? {
        val expirationInDays = configurationProperties.expirationInDays
        val batchSize = configurationProperties.batchSize
        if (expirationInDays == null || batchSize == null) {
            logger.info("No expiration value specified")
            return null
        }
        logger.info("Expiration value specified: $expirationInDays, batch size: $batchSize")
        val date = LocalDateTime.now().minusDays(expirationInDays.toLong())
        notificationRepository.deleteByCreatedBefore(date, batchSize)
        return null
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CleanupLambdaHandler::class.java)
    }

}
