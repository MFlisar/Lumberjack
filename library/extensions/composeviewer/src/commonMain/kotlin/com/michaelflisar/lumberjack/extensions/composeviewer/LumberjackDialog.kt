package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.Clipboard
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.io.files.Path

object LumberjackDialog {
    class State(
        val useScrollableLines: MutableState<Boolean>,
        val logFile: MutableState<Path?>,
        val logFileData: MutableState<Data>,
        val listState: LazyListState,
    )
}

@Composable
fun rememberLumberjackDialogState(
    style: LumberjackView.Style,
): LumberjackDialog.State {
    return LumberjackDialog.State(
        remember { mutableStateOf(style.singleScrollableLineView) },
        rememberLogFile(),
        rememberLogFileData(),
        rememberLazyListState()
    )
}

@Composable
fun LumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    setup: IFileLoggingSetup,
    style: LumberjackView.Style = LumberjackViewDefaults.style(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    feedbackConfig: FeedbackConfig? = null,
) {
    if (visible.value) {
        val state = rememberLumberjackDialogState(style)
        AlertDialog(
            onDismissRequest = { visible.value = false },
            title = {
                LumberjackTitleBar(
                    title = title,
                    setup = setup,
                    state = state,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AlertDialogDefaults.containerColor,
                        titleContentColor = AlertDialogDefaults.titleContentColor,
                        actionIconContentColor = AlertDialogDefaults.titleContentColor
                    ),
                    feedbackConfig = feedbackConfig
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { visible.value = false }
                ) {
                    Text("Close")
                }
            },
            text = {
                Column {
                    LumberjackView(
                        setup = setup,
                        file = state.logFile,
                        style = style,
                        data = state.logFileData,
                        modifier = Modifier.weight(1f),
                        state = state.listState,
                        darkTheme = darkTheme,
                        useScrollableLines = state.useScrollableLines
                    )
                }
            }
        )
    }
}

@Composable
fun LumberjackDialogContent(
    title: String,
    setup: IFileLoggingSetup,
    style: LumberjackView.Style = LumberjackViewDefaults.style(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    feedbackConfig: FeedbackConfig? = null,
) {
    val state = rememberLumberjackDialogState(style)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            LumberjackTitleBar(
                title = title,
                setup = setup,
                state = state,
                feedbackConfig = feedbackConfig
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LumberjackView(
                setup = setup,
                file = state.logFile,
                style = style,
                data = state.logFileData,
                modifier = Modifier.weight(1f),
                state = state.listState,
                darkTheme = darkTheme,
                useScrollableLines = state.useScrollableLines
            )
        }
    }
}