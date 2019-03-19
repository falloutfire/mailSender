package com.manny.mailApp.Controllers

import com.jfoenix.controls.JFXChipView
import com.manny.mailApp.Main
import com.manny.mailApp.Utils.EmailUtil
import com.manny.mailApp.Utils.User
import com.manny.mailApp.alertShowJfx
import com.manny.mailApp.loadingShow
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session

class MainLayoutController {

    var themeMail = "TLSEmail Testing Subject"
    var bodyMail = "TLSEmail Testing Body"
    var file: File? = null
    var user: User? = null

    lateinit var bodyMailArea: TextArea
    lateinit var toEmailView: JFXChipView<String>
    lateinit var themeField: TextField
    lateinit var fileLabel: Label

    private var main: Main? = null

    fun onClickSend() {

        if (user != null) {
            val listEmails = toEmailView.chips.toList()

            themeMail = themeField.text.toString()
            bodyMail = bodyMailArea.text.toString()

            if (EmailUtil().checkEmails(listEmails).isEmpty() && !listEmails.isEmpty()) {
                var alert = loadingShow(main?.primaryStage!!, true)
                alert.show()
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        val props = Properties()
                        props["mail.smtp.host"] = "smtp.gmail.com" //SMTP Host
                        props["mail.smtp.port"] = "587" //TLS Port
                        props["mail.smtp.auth"] = "true" //enable authentication
                        props["mail.smtp.starttls.enable"] = "true" //enable STARTTLS


                        //create Authenticator object to pass in Session.getInstance argument
                        val auth = object : Authenticator() {
                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                return PasswordAuthentication(user?.email, user?.password)
                            }
                        }

                        val session = Session.getInstance(props, auth)
                        EmailUtil()
                            .sendEmail(session, user?.email!!, user?.name!!, listEmails, themeMail, bodyMail, file)
                    }
                    delay(1000L)
                    alert.close()
                    alert = loadingShow(main?.primaryStage!!, false)
                    alert.show()
                    delay(1000L)
                    alert.close()
                }
            } else {
                alertShowJfx(
                    main?.primaryStage!!,
                    "Неправильно набраны email получателей",
                    "Проверьте правильность написания адресов получателей!"
                )
            }
        } else {
            alertShowJfx(
                main?.primaryStage!!,
                "Не указан отправитель!",
                "Пожалуйста, зайдите в настройки и укажите данные отправителя!"
            )
        }
    }


    fun onClickAttachFile() {
        val fileChooser = FileChooser()
        file = fileChooser.showOpenDialog(main?.primaryStage)
        file?.let {
            fileLabel.text = "Файл прикреплен\n${file!!.name}"
        }
    }

    fun setMainApp(main: Main) {
        this.main = main
    }
}
