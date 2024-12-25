package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.io.files.Path

object LumberjackDialog {
    class State(
        val useScrollableLines: MutableState<Boolean>,
        val logFile: MutableState<Path?>,
        val logFileData: MutableState<Data>,
        val listState: LazyListState
    )
}

@Composable
fun rememberLumberjackDialogState(
    style: LumberjackView.Style
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
    mail: String? = null
) {
    if (visible.value) {
        ShowLumberjackDialog(visible, title) {
            LumberjackDialogContent(title, setup, style, darkTheme, mail)
        }
    }
}

@Composable
fun LumberjackDialogContent(
    title: String,
    setup: IFileLoggingSetup,
    style: LumberjackView.Style = LumberjackViewDefaults.style(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    mail: String? = null
) {
    val state = rememberLumberjackDialogState(style)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBarImpl(title, setup, state, mail)
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