package com.manny.mailApp.Controllers

import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.stage.Stage
import kotlinx.coroutines.*
import com.manny.mailApp.Main
import com.manny.mailApp.Utils.EmailUtil
import com.manny.mailApp.Utils.User
import com.manny.mailApp.alertShowJfxTest
import com.manny.mailApp.loadingShow


class SettingsLayoutController {

    lateinit var fromEmailField: TextField
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
    }

    fun onClickClose() {
        dialogStage?.close()
    }

    fun onClickConnect() {
        user?.password = passwordField.text
        user?.email = fromEmailField.text

        var loader = loadingShow(dialogStage!!, true)
        loader.show()

        val isCorrect = runBlocking {
            EmailUtil().checkConnection(user!!)
        }
        if (isCorrect) {
            main?.user = user!!
            okClicked = true
        }
        GlobalScope.launch(Dispatchers.Main) {
            if (isCorrect) {
                delay(1000L)
                println("success")
                loader.close()
                loader = loadingShow(dialogStage!!, false)
                loader.show()
                delay(1500L)
                loader.close()
                dialogStage?.close()
            } else {
                delay(200L)
                loader.close()
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