package com.ess

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import io.micronaut.function.aws.MicronautRequestHandler
import jakarta.inject.Inject


class FunctionRequestHandler: MicronautRequestHandler<SQSEvent?, Void?>() {

    @Inject
    lateinit var emailService: EmailService

    override fun execute(input: SQSEvent?): Void? {
        if (input?.records != null) {
            for (sqsEvent in input.records) {
                val messageBody = sqsEvent.body
                println(messageBody)
                emailService.sendEmail("koroliuk.yaroslav@gmail.com", "Test", messageBody)
            }
        }
        return null
    }

}
