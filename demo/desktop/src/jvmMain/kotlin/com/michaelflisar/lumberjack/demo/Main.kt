package com.michaelflisar.lumberjack.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getAllExistingLogFiles
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

fun main() {

    // 1) install the implemantation
    L.init(LumberjackLogger)

    // 2) install loggers
    L.plant(ConsoleLogger())
    val setup = FileLoggerSetup.Daily.create(
        folder = File(System.getProperty("user.dir"))
    )
    L.plant(FileLogger(setup))

    application {

        val scope = rememberCoroutineScope()

        val recheckLogFiles = remember { mutableStateOf(0) }
        val logFiles by remember(recheckLogFiles.value) {
            derivedStateOf {
                setup.getAllExistingLogFiles()
            }
        }

        val showComposeLogView = rememberSaveable { mutableStateOf(false) }

        Window(
            title = "Lumberjack Demo",
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                width = 800.dp,
                height = 600.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Log Files:")
                logFiles.forEach {
                    Text(" - ${it.absolutePath}")
                }
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        L.d { "Button with debug log was clicked" }
                        L.tag("MAIN-TAG").d { "Button with debug log was clicked" }


                        delay(1000)
                        recheckLogFiles.value++
                    }
                }) {
                    Text("Log Debug")
                }
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            L.e { "Button with error log was clicked" }
                            L.tag("MAIN-TAG").e { "Button with error log was clicked" }

                            L.v { "verbose" }
                            L.i { "info" }
                            L.d { "debug" }
                            L.w { "warn" }
                            L.e { "error" }
                            L.wtf { "wtf" }

                            delay(1000)
                            recheckLogFiles.value++
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error
                    )
                ) {
                    Text("Log Error")
                }
                Button(
                    onClick = {
                        showComposeLogView.value = true
                    }
                ) {
                    Text("Show Log File")
                }
            }

            LumberjackDialog(
                visible = showComposeLogView,
                title = "Logs",
                setup = setup,
                darkTheme = MaterialTheme.colors.background.luminance() < .5,
                //mail = "...@gmail.com"
            )

        }
    }
}