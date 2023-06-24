package com.koroliuk.ess

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject


class EmailSenderLambdaHandler: MicronautRequestHandler<SQSEvent?, Void?>() {

    @Inject
    lateinit var emailService: EmailService

    override fun execute(input: SQSEvent?): Void? {
        val objectMapper = jacksonObjectMapper()

        input?.records?.forEach { sqsEvent ->
            val messageBody = sqsEvent.body

            try {
                val emailData: Map<String, String> = objectMapper.readValue(messageBody)

                val to = emailData["to"] ?: throw IllegalArgumentException("Missing 'to'")
                val subject = emailData["subject"] ?: throw IllegalArgumentException("Missing 'subject'")
                val message = emailData["message"] ?: throw IllegalArgumentException("Missing 'message'")

                emailService.sendEmail(to, subject, message)

            } catch (e: Exception) {
                println("Error parsing message body: ${e.message}")
            }
        }
        return null
    }

}
