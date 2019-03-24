package com.manny.mailApp.Controllers

import com.jfoenix.controls.JFXChipView
import com.manny.mailApp.Main
import com.manny.mailApp.Utils.EmailUtil
import com.manny.mailApp.Utils.User
import com.manny.mailApp.alertShowJfx
import com.manny.mailApp.alertShowJfxTest
import com.manny.mailApp.loadingShow
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.FileChooser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

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
                    val isAvaliable = run {
                        EmailUtil().netIsAvailable()
                    }
                    if (isAvaliable) {
                        val isSend = run {
                            EmailUtil().sendEmailToServer(
                                user?.email!!,
                                user?.password!!,
                                user?.name!!,
                                listEmails,
                                themeMail,
                                bodyMail,
                                file
                            )
                        }
                        if (isSend) {
                            delay(1000L)
                            alert.close()
                            alert = loadingShow(main?.primaryStage!!, false)
                            alert.show()
                            delay(1000L)
                            alert.close()
                        } else {
                            delay(1000L)
                            alert.close()
                            alertShowJfx(
                                main?.primaryStage!!,
                                "Сервер недоступен",
                                "Пожалуйста, попробуйте повторить операцию через некоторое время."
                            )
                        }
                    }
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
            if (it.length() <= 1024 * 1024 * 10) {
                fileLabel.text = "Файл прикреплен\n${file!!.name}"
            } else {
                alertShowJfxTest(main?.primaryStage!!, "Ошибка", "Файл не может быть больше 10 Мб").showAndWait()
                file = null
            }
        }
    }

    fun setMainApp(main: Main) {
        this.main = main
    }
}
