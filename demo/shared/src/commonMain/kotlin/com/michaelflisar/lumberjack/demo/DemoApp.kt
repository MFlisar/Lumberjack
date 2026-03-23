package com.michaelflisar.lumberjack.demo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.michaelflisar.democomposables.layout.DemoCollapsibleRegion
import com.michaelflisar.democomposables.layout.DemoColumn
import com.michaelflisar.democomposables.layout.DemoRegion
import com.michaelflisar.democomposables.layout.rememberDemoExpandedRegions
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.io.files.SystemFileSystem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp(
    platform: String,
    setup: IFileLoggingSetup?,
    ioContext: CoroutineDispatcher,
    sendFeedback: ((receiver: String) -> Unit)? = null,
) {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lumberjack Demo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            DemoContent(
                modifier = Modifier.padding(paddingValues),
                platform = platform,
                setup = setup,
                ioContext = ioContext,
                sendFeedback = sendFeedback,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun DemoContent(
    modifier: Modifier,
    platform: String,
    setup: IFileLoggingSetup?,
    ioContext: CoroutineDispatcher,
    sendFeedback: ((String) -> Unit)?,
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val showComposeLogView = rememberSaveable { mutableStateOf(false) }
    val regionState = rememberDemoExpandedRegions(ids = listOf(1, 2))
    var mail by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        DemoLogging.run(this, ioContext)
    }

    DemoColumn(
        modifier = modifier.padding(all = 16.dp)
    ) {
        DemoRegion("Platform: ($platform)")
        DemoCollapsibleRegion(
            title = "Demos", regionId = 1, state = regionState
        ) {
            //OutlinedButton(
            //    modifier = Modifier.fillMaxWidth(),
            //    onClick = { L.showLog(context, setup, mail) }
            //) {
            //    Text("Log Viewer (View)")
            //}
            if (setup != null) {
                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    showComposeLogView.value = true
                }) {
                    Text("Log Viewer")
                }
            } else {
                Text("Log Viewer requires a FileLoggingSetup to be configured! This platform does not support it.")
            }
        }
        DemoCollapsibleRegion(
            title = "Actions", regionId = 2, state = regionState
        ) {
            if (sendFeedback != null) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = mail,
                    onValueChange = { mail = it },
                    label = {
                        Text("Receiver Mail")
                    }
                )
            }
            if (setup != null) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            setup.clearLogFiles()
                            L.d { "TEST-LOG - Files just have been deleted!" }
                        }
                    }
                ) {
                    Text("Delete Log Files")
                }
            }
            if (sendFeedback != null) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        mail.takeIf { it.isNotEmpty() }?.let {
                            sendFeedback(it)
                        } ?:
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "You must provide a valid email address to test this function!"
                            )
                        }
                    }
                ) {
                    Text("Send log file via mail")
                }
            }
            if (setup != null) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        L.d { "Logging something..." }
                        val path = setup.getLatestLogFilePath()
                        val pathExists = path?.let { SystemFileSystem.exists(it) }
                        L.d { "Log file - path = ${path} | exists = $pathExists" }

                    }
                ) {
                    Text("Log something")
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch(ioContext) {
                        (1..100).forEach {
                            L.d { "Logging a lot $it..." }
                        }
                    }
                }
            ) {
                Text("Log a lot")
            }
        }
    }
    if (setup != null) {
        LumberjackDialog(
            visible = showComposeLogView,
            title = "Logs",
            setup = setup,
            darkTheme = false,
            feedbackConfig = mail.takeIf { it.isNotEmpty() }?.let {
                FeedbackConfig.create(
                    receiver = it,
                    appName = "Lumberjack Demo",
                    appVersion = "1.0.0"
                )
            }
        )
    }
}