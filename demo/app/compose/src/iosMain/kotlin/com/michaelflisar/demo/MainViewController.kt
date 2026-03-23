package com.michaelflisar.demo

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.michaelflisar.lumberjack.demo.DemoApp
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * iOS entry point used by the Xcode demo project (demo/xcode).
 */
fun MainViewController() = ComposeUIViewController {
    val setup = remember { FileLoggerSetup.Daily.create(fileBaseName = "log_daily") }
    val init = remember { mutableStateOf(false) }

    LaunchedEffect(init.value) {
        if (!init.value) {
            DemoLogging.init(FileLogger(setup))
            init.value = true
        }
    }

    if (!init.value) {
        // avoid showing UI before logging is initialized in this demo...
        return@ComposeUIViewController
    }

    DemoApp(
        platform = "iOS",
        setup = setup,
        ioContext = Dispatchers.IO,
        sendFeedback = {
            MailSender.sendMail(
                receiver = it,
                attachments = setup.getLatestLogFilePath()?.toString()?.let { listOf(it) } ?: emptyList()
            )
        }
    )
}
