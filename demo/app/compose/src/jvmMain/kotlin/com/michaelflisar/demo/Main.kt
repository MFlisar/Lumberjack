package com.michaelflisar.demo

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getAllExistingLogFiles
import com.michaelflisar.lumberjack.demo.DemoApp
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import kotlinx.coroutines.Dispatchers
import java.io.File

fun main() {

    val setup = FileLoggerSetup.Daily.create(
        folder = File(System.getProperty("user.dir")),
        fileExtension = "txt"
    )
    DemoLogging.init(FileLogger(setup))

    L.tag("Tag1").d { "before main application" }
    DemoLogging.runTest()

    application {

        LaunchedEffect(Unit) {
            try {
                val x = 1 / 0
            } catch (e: Exception) {
                L.e(e) { "x = 1 / 0" }
            }
        }
        Window(
            title = "Lumberjack Demo",
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                width = 800.dp,
                height = 600.dp
            )
        ) {
            DemoApp(
                platform = "Windows",
                setup = setup,
                ioContext = Dispatchers.IO,
                sendFeedback = null // only works on android currently
            )
        }
    }
}