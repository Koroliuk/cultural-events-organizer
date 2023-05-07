package com.koroliuk.ncs

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject
import java.time.LocalDateTime


class FunctionRequestHandler: MicronautRequestHandler<ScheduledEvent?, Void?>() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var cleanupProperties: CleanupProperties

    override fun execute(input: ScheduledEvent?): Void? {
        val expirationInDays = cleanupProperties.expirationInDays
        if (expirationInDays == 0) {
            println("No expiration value specified")
            return null
        }
        println("Expiration value specified $expirationInDays")
        val date = LocalDateTime.now().minusDays(expirationInDays!!.toLong())
        notificationRepository.deleteByCreatedBefore(date)
        return null
    }

}
