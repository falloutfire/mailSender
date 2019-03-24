package com.manny.mailApp.Controllers

import com.jfoenix.controls.JFXAlert
import com.manny.mailApp.Main
import com.manny.mailApp.Utils.EmailUtil
import com.manny.mailApp.Utils.User
import com.manny.mailApp.alertShowJfx
import com.manny.mailApp.alertShowJfxTest
import com.manny.mailApp.loadingShow
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.stage.Stage
import kotlinx.coroutines.*


class SettingsLayoutController {

    lateinit var fromEmailField: TextField
    lateinit var nameField: TextField
    lateinit var passwordField: PasswordField

    private var main: Main? = null
    private var dialogStage: Stage? = null
    private var user: User? = null
    var okClicked = false

    fun setDialogStage(dialogStage: Stage) {
        this.dialogStage = dialogStage
    }

    fun setMain(main: Main) {
        this.main = main
        this.user = main.user
        fromEmailField.text = user?.email
        passwordField.text = user?.password
        nameField.text = user?.name
    }

    fun onClickClose() {
        dialogStage?.close()
    }

    fun onClickConnect() {
        if (fromEmailField.text != null && passwordField.text != null && nameField.text != null) {
            user?.password = passwordField.text
            user?.email = fromEmailField.text
            user?.name = nameField.text

            var loader: JFXAlert<Void>? = null
            GlobalScope.launch(Dispatchers.Main) {
                loader = loadingShow(dialogStage!!, true)
                loader?.show()
            }
            runBlocking(Dispatchers.Default) {
                EmailUtil().netIsAvailable()
            }.also {
                if (!it) {
                    GlobalScope.launch(Dispatchers.Main) {
                        delay(200L)
                        loader?.close()
                        val alert =
                            alertShowJfxTest(
                                dialogStage!!,
                                "Ошибка соединения",
                                "Проверьте ваше интернет подключение!"
                            )
                        alert.show()
                        delay(8000L)
                        alert.close()
                    }
                } else {
                    val isCorrect = runBlocking(Dispatchers.Default) {
                        EmailUtil().checkConnection(user!!)
                    }.also { corr ->
                        if (corr) {
                            main?.user = user!!
                            okClicked = true
                        }
                    }
                    GlobalScope.launch(Dispatchers.Main) {
                        if (isCorrect) {
                            delay(1000L)
                            println("success")
                            loader?.close()
                            loader = loadingShow(dialogStage!!, false)
                            loader?.show()
                            delay(1500L)
                            loader?.close()
                            dialogStage?.close()
                        } else {
                            delay(200L)
                            loader?.close()
                            val alert =
                                alertShowJfxTest(
                                    dialogStage!!,
                                    "Ошибка соединения",
                                    "Проверьте правильность введенных данных!"
                                )
                            alert.show()
                            delay(8000L)
                            alert.close()
                        }
                    }
                }
            }
        } else {
            alertShowJfx(
                main?.primaryStage!!,
                "Отсутствуют данные отправителя",
                "Проверьте правильность написания данных отправителя!"
            )
        }
    }
}