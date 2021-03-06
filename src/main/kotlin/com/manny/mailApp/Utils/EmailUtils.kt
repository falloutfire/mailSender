package com.manny.mailApp.Utils

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.HttpHostConnectException
import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultHttpClient
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.mail.AuthenticationFailedException
import javax.mail.Session


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

    suspend fun sendEmailToServer(
        fromEmail: String,
        password: String,
        name: String,
        toEmail: List<String>,
        subject: String,
        body: String,
        file: File?
    ): Boolean {
        val httpclient = DefaultHttpClient()
        //val httppost = HttpPost("https://mail-server-spring.herokuapp.com/mail/sendEmailWithAttach")
        val httppost = HttpPost("http://localhost:8080/")
        val reqEntity = MultipartEntity()

        file?.let {
            val fileSend = FileBody(file)
            reqEntity.addPart("file", fileSend)
        }

        val arrayEmail = toEmail.map { User(it) }
        val mail = Mail(User(fromEmail, name, password), arrayEmail, subject, body)
        val mapper = ObjectMapper()

        reqEntity.addPart("mail", StringBody(mapper.writeValueAsString(mail)))
        httppost.entity = reqEntity

        withContext(Dispatchers.IO) {
            val response: HttpResponse?
            try {
                response = httpclient.execute(httppost)
                val resEntity = response.entity
                val codeRes = response.statusLine.statusCode
                val bodyRes = response.entity.content
                val textBuilder = StringBuilder()
                var c = 0
                c = bodyRes.read()
                while (c != -1) {
                    textBuilder.append(c.toChar())
                    c = bodyRes.read()
                }
                println(codeRes)
                println(textBuilder)
                return@withContext true
            } catch (e: HttpHostConnectException) {
                return@withContext false
            }
        }
        return false
    }

    suspend fun checkConnection(user: User): Boolean = withContext(Dispatchers.Default) {
        val port = 587
        val host = "smtp.gmail.com"
        val props = Properties()
        props["mail.smtp.host"] = "smtp.gmail.com" //SMTP Host
        props["mail.smtp.port"] = "587" //TLS Port
        props["mail.smtp.auth"] = "true" //enable authentication
        props["mail.smtp.starttls.enable"] = "true" //enable STARTTLS
        try {
            //create Authenticator object to pass in Session.getInstance argument

            val session = Session.getInstance(props, null)
            val transport = session.getTransport("smtp")
            transport.connect(host, port, user.email, user.password)
            transport.close()
            return@withContext true
        } catch (e: AuthenticationFailedException) {
            println("AuthenticationFailedException - for authentication failures")
            return@withContext false
        }
    }

    suspend fun netIsAvailable(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL("http://www.google.com")
            val conn = url.openConnection()
            conn.connect()
            conn.getInputStream().close()
            true
        } catch (e: MalformedURLException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            false
        }

    }
}