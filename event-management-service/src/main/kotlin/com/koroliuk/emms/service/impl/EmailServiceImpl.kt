package com.koroliuk.emms.service.impl

import com.koroliuk.emms.service.EmailService
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import java.util.*


@Singleton
class EmailServiceImpl : EmailService {

    @Value("\${aws.sqs.name}")
    lateinit var queueUrl: String

    override fun sendMessageToQueue(text: String, sendTo: String) {
        val sendMessageRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageBody("{\n" +
                    "  \"to\": \"$sendTo\",\n" +
                    "  \"subject\": \"Event\",\n" +
                    "  \"message\" : \"$text\"\n" +
                    "}")
            .messageGroupId(UUID.randomUUID().toString())
            .messageDeduplicationId(UUID.randomUUID().toString())
            .build()

        val sendMessageResponse = sqsClient.sendMessage(sendMessageRequest)
        println("Message ID: ${sendMessageResponse.messageId()}")
    }

    companion object {

        val sqsClient: SqsClient = SqsClient.builder()
            .region(Region.EU_CENTRAL_1)
            .build()

    }

}
