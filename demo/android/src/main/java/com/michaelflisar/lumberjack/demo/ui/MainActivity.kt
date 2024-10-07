package com.michaelflisar.lumberjack.demo.ui

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import com.michaelflisar.demoutilities.DemoActivity
import com.michaelflisar.demoutilities.composables.DemoAppThemeRegionDetailed
import com.michaelflisar.demoutilities.composables.DemoCollapsibleRegion
import com.michaelflisar.demoutilities.composables.ExpandedRegionState
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.defaultScrim
import com.michaelflisar.composethemer.enableEdgeToEdge
import com.michaelflisar.demoutilities.classes.ToastHelper
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getLatestLogFiles
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.extensions.composeviewer.LumberjackDialog
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import com.michaelflisar.lumberjack.extensions.viewer.showLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : DemoActivity(
    scrollableContent = false
) {

    @Composable
    override fun ColumnScope.Content(
        regionsState: ExpandedRegionState,
        themeState: ComposeTheme.State
    ) {

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
            state = regionsState
        )
        DemoCollapsibleRegion(
            title = "Demos",
            regionId = 1,
            state = regionsState
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
            if (DemoLogging.FILE_LOGGING_SETUP2 != null) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showComposeLogView2.value = true
                    }) {
                    Text("Log Viewer (Compose) - Setup2")
                }
            }
        }
        DemoCollapsibleRegion(
            title = "Actions",
            regionId = 2,
            state = regionsState
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
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        (1..100).forEach {
                            L.d { "Logging a lot $it..." }
                        }
                    }
                }) {
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

    fun Context.getActivity(): ComponentActivity? = when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }
}