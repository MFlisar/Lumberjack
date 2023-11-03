package com.michaelflisar.lumberjack.demo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedemobaseactivity.DemoBaseActivity
import com.michaelflisar.composedemobaseactivity.classes.DemoTheme
import com.michaelflisar.composedemobaseactivity.classes.ToastHelper
import com.michaelflisar.composedemobaseactivity.classes.listSaverKeepEntryStateList
import com.michaelflisar.composedemobaseactivity.composables.DemoAppThemeRegion
import com.michaelflisar.composedemobaseactivity.composables.DemoCollapsibleRegion
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import kotlinx.coroutines.launch

class MainActivity : DemoBaseActivity() {

    @Composable
    override fun Content(modifier: Modifier, theme: DemoTheme, dynamicTheme: Boolean) {

        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            DemoLogging.run(scope)
        }

        val context = LocalContext.current
        val expandedRegions = rememberSaveable(Unit, saver = listSaverKeepEntryStateList()) {
            mutableStateListOf(1)
        }
        var mail by rememberSaveable {
            mutableStateOf("")
        }
        val showComposeLogView = rememberSaveable {
            mutableStateOf(false)
        }

        Column(
            modifier = modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DemoAppThemeRegion(
                theme,
                dynamicTheme,
                id = 0,
                expandedIds = expandedRegions
            )
            DemoCollapsibleRegion(
                title = "Demos",
                id = 1,
                expandedIds = expandedRegions
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        L.showLog(context, DemoLogging.FILE_LOGGING_SETUP, mail)
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
            DemoCollapsibleRegion(
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
                            DemoLogging.FILE_LOGGING_SETUP.clearLogFiles()
                            L.d { "TEST-LOG - Files just have been deleted!" }
                        }
                    }) {
                    Text("Delete Log Files")
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mail.takeIf { it.isNotEmpty() }?.let {
                            L.sendFeedback(
                                context = context,
                                receiver = it,
                                attachments = listOfNotNull(DemoLogging.FILE_LOGGING_SETUP.getLatestLogFiles())
                            )
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
            visible = showComposeLogView,
            title = "Logs",
            setup = DemoLogging.FILE_LOGGING_SETUP,
            darkTheme = theme.isDark(),
            //mail = "...@gmail.com"
        )
    }
}