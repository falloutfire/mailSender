package com.manny.mailApp.Controllers

import com.manny.mailApp.Main

class RootLayoutController {

    private var main: Main? = null

    fun setMainApp(main: Main) {
        this.main = main
    }

    fun onClickSettings() {
        val okClicked = main!!.showSettingsDialog()
        if (okClicked) {
            main?.mainLoader?.user = main?.user
        }
    }

}