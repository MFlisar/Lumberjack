package com.michaelflisar.lumberjack.demo.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getLatestLogFile
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoAppThemeRegionDetailed
import com.michaelflisar.toolbox.androiddemoapp.composables.DemoCollapsibleRegion
import com.michaelflisar.toolbox.androiddemoapp.composables.rememberDemoExpandedRegions
import com.michaelflisar.toolbox.classes.ToastHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.files.SystemFileSystem

class MainActivity : com.michaelflisar.toolbox.androiddemoapp.DemoActivity(
    scrollableContent = false
) {

    @Composable
    override fun ColumnScope.Content(
        themeState: ComposeTheme.State
    ) {
        val regionState = rememberDemoExpandedRegions()

        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            DemoLogging.run(scope)
        }

        val context = LocalContext.current

        var mail by rememberSaveable {
            mutableStateOf("")
        }
        val showComposeLogView = rememberSaveable { mutableStateOf(false) }
        val showComposeLogView2 = rememberSaveable { mutableStateOf(false) }

        DemoAppThemeRegionDetailed(
            state = regionState
        )
        DemoCollapsibleRegion(
            title = "Demos", regionId = 1, state = regionState
        ) {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                L.showLog(context, DemoLogging.FILE_LOGGING_SETUP, mail)
            }) {
                Text("Log Viewer (View)")
            }
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                showComposeLogView.value = true
            }) {
                Text("Log Viewer (Compose)")
            }
            if (DemoLogging.FILE_LOGGING_SETUP2 != null) {
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    showComposeLogView2.value = true
                }) {
                    Text("Log Viewer (Compose) - Setup2")
                }
            }
        }
        DemoCollapsibleRegion(
            title = "Actions", regionId = 2, state = regionState
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = mail,
                onValueChange = { mail = it },
                label = {
                    Text("Receiver Mail")
                }
            )
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        DemoLogging.FILE_LOGGING_SETUP.clearLogFiles()
                        L.d { "TEST-LOG - Files just have been deleted!" }
                    }
                }
            ) {
                Text("Delete Log Files")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    mail.takeIf { it.isNotEmpty() }?.let {
                        L.sendFeedback(
                            context = context,
                            receiver = it,
                            attachments = listOfNotNull(DemoLogging.FILE_LOGGING_SETUP.getLatestLogFile())
                        )
                    } ?: ToastHelper.show(
                        context, "You must provide a valid email address to test this function!"
                    )
                }
            ) {
                Text("Send log file via mail")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    L.d { "Logging something..." }
                    val path = DemoLogging.FILE_LOGGING_SETUP.getLatestLogFilePath()
                    val file = DemoLogging.FILE_LOGGING_SETUP.getLatestLogFile()
                    L.d { "Log file - path = ${path} | exists = ${path?.let { SystemFileSystem.exists(it) }}" }
                    L.d { "Log file - file = ${file?.absolutePath} | exists = ${file?.exists()}" }

                }
            ) {
                Text("Log something")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        (1..100).forEach {
                            L.d { "Logging a lot $it..." }
                        }
                    }
                }
            ) {
                Text("Log a lot")
            }
        }

        LumberjackDialog(
            visible = showComposeLogView,
            title = "Logs",
            setup = DemoLogging.FILE_LOGGING_SETUP,
            darkTheme = themeState.base.value.isDark(),
            //mail = "...@gmail.com"
        )

        if (DemoLogging.FILE_LOGGING_SETUP2 != null) {
            LumberjackDialog(
                visible = showComposeLogView2,
                title = "Logs",
                setup = DemoLogging.FILE_LOGGING_SETUP2!!,
                darkTheme = themeState.base.value.isDark(),
                //mail = "...@gmail.com"
            )
        }
    }
}