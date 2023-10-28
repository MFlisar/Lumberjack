package com.michaelflisar.lumberjack.demo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedemobaseactivity.DemoActivity
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import com.michaelflisar.composedemobaseactivity.classes.ToastHelper
import com.michaelflisar.composedemobaseactivity.composables.CollapsibleRegion
import com.michaelflisar.composedemobaseactivity.composables.DemoAppThemeRegion
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.demo.App
import com.michaelflisar.lumberjack.demo.LogTests
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import kotlinx.coroutines.launch


class MainActivity : DemoActivity() {

    @Composable
    override fun Content(modifier: Modifier, theme: DemoTheme, dynamicTheme: Boolean) {

        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            LogTests.run(scope)
        }

        val context = LocalContext.current
        val expandedRegions = rememberSaveable(saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )) {
            mutableStateListOf(1)
        }
        var mail by rememberSaveable {
            mutableStateOf("")
        }
        val showComposeLogView = rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DemoAppThemeRegion(
                theme,
                dynamicTheme,
                id = 0,
                expandedIds = expandedRegions
            )
            CollapsibleRegion(
                title = "Demos",
                id = 1,
                expandedIds = expandedRegions
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        L.showLog(context, App.FILE_LOGGING_SETUP, mail)
                    }) {
                    Text("Log Viewer (View)")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showComposeLogView.value = true
                    }) {
                    Text("Log Viewer (Compose)")
                }
            }
            CollapsibleRegion(
                title = "Actions",
                id = 2,
                expandedIds = expandedRegions
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = mail,
                    onValueChange = { mail = it },
                    label = {
                        Text("Receiver Mail")
                    })
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            App.FILE_LOGGING_SETUP.clearLogFiles()
                            L.d { "TEST-LOG - Files just have been deleted!" }
                        }
                    }) {
                    Text("Delete Log Files")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mail.takeIf { it.isNotEmpty() }?.let {
                            L.sendFeedback(context, App.FILE_LOGGING_SETUP.getLatestLogFiles(), it)
                        } ?: ToastHelper.show(
                            context,
                            "You must provide a valid email address to test this function!"
                        )
                    }) {
                    Text("Send log file via mail")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        L.d { "Logging something..." }
                    }) {
                    Text("Log something")
                }
            }
        }

        LumberjackDialog(
            showComposeLogView,
            "Logs",
            App.FILE_LOGGING_SETUP,
            darkTheme = theme.isDark()
        )
    }
}