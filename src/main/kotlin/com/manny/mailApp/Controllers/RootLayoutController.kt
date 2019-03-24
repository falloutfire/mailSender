package com.manny.mailApp.Controllers

import com.manny.mailApp.Main
import javafx.stage.FileChooser


class RootLayoutController {

    private var main: Main? = null

    fun setMainApp(main: Main) {
        this.main = main
    }

    fun onClickTest() {

        val fileChooser = FileChooser()
        val file = fileChooser.showOpenDialog(main?.primaryStage)

    }

    fun onClickSettings() {
        val okClicked = main!!.showSettingsDialog()
        if (okClicked) {
            main?.mainLoader?.user = main?.user
        }
    }

}