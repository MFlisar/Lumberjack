package com.michaelflisar.lumberjack.demo.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.michaelflisar.democomposables.layout.DemoCollapsibleRegion
import com.michaelflisar.democomposables.layout.DemoColumn
import com.michaelflisar.democomposables.layout.rememberDemoExpandedRegions
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getLatestLogFile
import com.michaelflisar.lumberjack.demo.R
import com.michaelflisar.lumberjack.demo.App
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.files.SystemFileSystem

class DemoActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) { paddingValues ->
                    Content(Modifier
                        .padding(paddingValues)
                        .padding(all = 16.dp)
                        .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier
) {
    val regionState = rememberDemoExpandedRegions()

    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        DemoLogging.run(this, Dispatchers.IO)
    }

    val context = LocalContext.current

    var mail by rememberSaveable {
        mutableStateOf("")
    }
    val showComposeLogView = rememberSaveable { mutableStateOf(false) }

    DemoColumn(
        modifier = modifier
    ) {

        DemoCollapsibleRegion(
            title = "Demos", regionId = 1, state = regionState
        ) {
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                L.showLog(context, App.setup, mail)
            }) {
                Text("Log Viewer (View)")
            }
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                showComposeLogView.value = true
            }) {
                Text("Log Viewer (Compose)")
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
                        App.setup.clearLogFiles()
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
                            attachments = listOfNotNull(App.setup.getLatestLogFile())
                        )
                    } ?: Toast.makeText(
                        context,
                        "You must provide a valid email address to test this function!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ) {
                Text("Send log file via mail")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    L.d { "Logging something..." }
                    val path = App.setup.getLatestLogFilePath()
                    val file = App.setup.getLatestLogFile()
                    L.d {
                        "Log file - path = ${path} | exists = ${
                            path?.let {
                                SystemFileSystem.exists(
                                    it
                                )
                            }
                        }"
                    }
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
    }

    LumberjackDialog(
        visible = showComposeLogView,
        title = "Logs",
        setup = App.setup,
        darkTheme = false,
        //mail = "...@gmail.com"
    )
}