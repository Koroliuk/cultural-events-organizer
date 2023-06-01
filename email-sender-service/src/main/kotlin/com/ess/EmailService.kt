package com.ess

import jakarta.inject.Singleton
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties

@Singleton
class EmailService {
    fun sendEmail(to: String, subject: String, text: String) {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.elasticemail.com"
        props["mail.smtp.port"] = "2525"

        val session = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication("tt6761826@gmail.com", "94033C90C1FD8685A14087CE4E8A49927E16")
                }
            })

        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress("tt6761826@gmail.com"))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            message.subject = subject
            message.setText(text)

            Transport.send(message)

            println("Done")

        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
    }
}
