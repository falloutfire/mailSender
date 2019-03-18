package com.manny.mailApp.Utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailUtil {

    fun checkEmails(listEmails: List<String>): ArrayList<String> {
        val checkEmails = ArrayList<String>()
        listEmails.forEach {
            if (!it.matches("^\\w+[\\w-.]*@\\w+((-\\w+)|(\\w*))\\.[a-z]{2,3}\$".toRegex())) {
                checkEmails.add(it)
            }

        }
        return checkEmails
    }

    fun sendEmail(
        session: Session,
        fromEmail: String,
        themeMail: String,
        toEmail: List<String>,
        subject: String,
        body: String,
        file: File?
    ) {
        try {
            toEmail.forEach {
                val msg = MimeMessage(session)
                //set message headers
                msg.addHeader("Content-type", "text/HTML; charset=UTF-8")
                msg.addHeader("format", "flowed")
                msg.addHeader("Content-Transfer-Encoding", "8bit")

                msg.setFrom(InternetAddress(fromEmail, themeMail))
                msg.replyTo = InternetAddress.parse(it, false)

                msg.setSubject(subject, "UTF-8")

                msg.sentDate = Date()

                if (file != null) {
                    // Create a multipart message for attachment
                    val multipart = MimeMultipart()

                    // Create the message body part
                    var messageBodyPart: BodyPart = MimeBodyPart()

                    // Fill the message
                    messageBodyPart.setText(body)

                    // Set text message part
                    multipart.addBodyPart(messageBodyPart)

                    // Second part is attachment
                    messageBodyPart = MimeBodyPart()
                    val source = FileDataSource(file)
                    messageBodyPart.dataHandler = DataHandler(source)
                    messageBodyPart.fileName = file.name
                    multipart.addBodyPart(messageBodyPart)

                    // Send the complete message parts
                    msg.setContent(multipart)
                } else {
                    msg.setText(body, "UTF-8")
                }

                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(it, false))
                println("Message is ready")
                Transport.send(msg)

                println("EMail Sent Successfully!!")
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun checkConnection(user: User): Boolean = withContext(Dispatchers.Default) {
        try {

            val port = 587
            val host = "smtp.gmail.com"
            val props = Properties()
            // required for gmail
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.auth"] = "true"
            // or use getDefaultInstance instance if desired...
            val session = Session.getInstance(props, null)
            val transport = session.getTransport("smtp")
            transport.connect(host, port, user.email, user.password)
            transport.close()
            return@withContext true
        } catch (e: AuthenticationFailedException) {
            println("AuthenticationFailedException - for authentication failures")
            e.printStackTrace()
            return@withContext false
        }
    }
}