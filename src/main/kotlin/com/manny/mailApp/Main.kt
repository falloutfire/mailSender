package com.manny.mailApp

import com.jfoenix.animation.alert.JFXAlertAnimation
import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.controls.JFXSpinner
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import com.manny.mailApp.Controllers.MainLayoutController
import com.manny.mailApp.Controllers.RootLayoutController
import com.manny.mailApp.Controllers.SettingsLayoutController
import com.manny.mailApp.Utils.User
import java.io.IOException


class Main : Application() {
    var primaryStage: Stage? = null
    var rootLoader: RootLayoutController? = null
    var mainLoader: MainLayoutController? = null
    var user: User = User()
    private var rootPane: BorderPane? = null

    override fun start(primaryStage: Stage?) {
        this.primaryStage = primaryStage
        this.primaryStage?.sizeToScene()
        this.primaryStage?.title = "Mail app"
        this.primaryStage?.isResizable = false
        initRootLayout()
        showMainLayout()
    }

    @Throws(IOException::class)
    private fun showMainLayout() {

        val loader = FXMLLoader()
        loader.location = Main::class.java.getResource("Views/MainLayout.fxml")

        val mainPane = loader.load<AnchorPane>() as AnchorPane
        rootPane?.center = mainPane

        mainLoader = loader.getController<MainLayoutController>()
        mainLoader?.setMainApp(this)
    }

    @Throws(IOException::class)
    private fun initRootLayout() {
        val loader = FXMLLoader()
        loader.location = Main::class.java.getResource("Views/RootLayout.fxml")
        rootPane = loader.load<BorderPane>() as BorderPane

        val scene = Scene(rootPane)
        primaryStage?.scene = scene
        primaryStage?.show()

        rootLoader = loader.getController()
        rootLoader?.setMainApp(this)
    }

    @Throws(IOException::class)
    fun showSettingsDialog(): Boolean {
        try {
            val loader = FXMLLoader()
            loader.location = Main::class.java.getResource("Views/SettingsLayout.fxml")
            val pane = loader.load<AnchorPane>()

            val dialogStage = Stage()
            dialogStage.title = "Настройки"
            dialogStage.initStyle(StageStyle.UNDECORATED)
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(primaryStage)
            val scene = Scene(pane)
            dialogStage.scene = scene
            dialogStage.isResizable = false

            val controller = loader.getController<SettingsLayoutController>()
            controller.setDialogStage(dialogStage)
            controller.setMain(this)

            dialogStage.showAndWait()
            return controller.okClicked
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

    }
}

fun alertShowJfxTest(dialogStage: Stage, header: String, content: String): JFXAlert<Void> {
    val labelHeader = Label(header)

    val labelText = Label(content)

    val layout = JFXDialogLayout()
    layout.setHeading(labelHeader)
    layout.setBody(labelText)

    val btn = JFXButton("OK")
    btn.setMinSize(100.0, 35.0)
    btn.style = "-fx-background-color: #4876ff;"
    btn.textFill = Paint.valueOf("#FFFFFF")

    val alert = JFXAlert<Void>(dialogStage)
    btn.setOnAction { alert.close() }
    layout.setActions(btn)
    alert.isOverlayClose = true
    alert.animation = JFXAlertAnimation.CENTER_ANIMATION
    alert.setContent(layout)
    alert.initModality(Modality.NONE)
    return alert
}

fun alertShowJfx(dialogStage: Stage, header: String, content: String) {
    val labelHeader = Label(header)

    val labelText = Label(content)

    val layout = JFXDialogLayout()
    layout.setHeading(labelHeader)
    layout.setBody(labelText)

    val btn = JFXButton("OK")
    btn.setMinSize(100.0, 35.0)
    btn.style = "-fx-background-color: #4876ff;"
    btn.textFill = Paint.valueOf("#FFFFFF")

    val alert = JFXAlert<Void>(dialogStage)
    btn.setOnAction { alert.close() }
    layout.setActions(btn)
    alert.isOverlayClose = true
    alert.animation = JFXAlertAnimation.CENTER_ANIMATION
    alert.setContent(layout)
    alert.initModality(Modality.NONE)
    alert.showAndWait()
}

fun loadingShow(dialogStage: Stage, spin: Boolean): JFXAlert<Void> {
    val layout = JFXDialogLayout()
    if (spin) {
        val spinner = JFXSpinner()
        layout.setBody(spinner)
    } else {
        layout.setBody(Label("Операция выполнена"))
    }
    val alert = JFXAlert<Void>(dialogStage)
    alert.isOverlayClose = true
    alert.animation = JFXAlertAnimation.CENTER_ANIMATION
    alert.setContent(layout)
    alert.initModality(Modality.NONE)
    return alert
}

fun main() {
    Application.launch(Main::class.java)
}