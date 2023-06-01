package com.ems.controller

import io.micronaut.context.annotation.Value
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.util.UUID

@Controller("/api/email")
@Secured("USER")
class EmailController {

    @Value("\${aws.sqs.name}")
    lateinit var queueUrl: String

    @Post
    fun sendEmail() {
        val sqsClient = SqsClient.builder()
            .region(Region.EU_CENTRAL_1)
            .build()

        val sendMessageRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody("Hello, SQS!")
            .messageGroupId(UUID.randomUUID().toString())
            .messageDeduplicationId(UUID.randomUUID().toString())
            .build()

        val sendMessageResponse = sqsClient.sendMessage(sendMessageRequest)
        println("Message ID: ${sendMessageResponse.messageId()}")
    }

}