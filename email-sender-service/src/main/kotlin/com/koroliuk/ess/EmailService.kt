package com.koroliuk.ess

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties

@Singleton
class EmailService(

    @Value("\${email.send-from}") private val sendFrom: String,
    @Value("\${smtp.server}") private val server: String,
    @Value("\${smtp.port}") private val port: String,
    @Value("\${smtp.username}") private val username: String,
    @Value("\${smtp.password}") private val password: String
) {

    private val session: Session

    init {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", server)
            put("mail.smtp.port", port)
        }

        session = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(username, password)
                }
            })
    }

    fun sendEmail(to: String, subject: String, text: String) {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(sendFrom))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                this.subject = subject
                setText(text)
            }
            Transport.send(message)
            logger.info("Done")
        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EmailService::class.java)
    }

}
