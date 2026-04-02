package com.michaelflisar.demo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.demo.BuildKonfig
import com.michaelflisar.lumberjack.demo.DemoApp
import com.michaelflisar.lumberjack.demo.DemoLogging
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    DemoLogging.init(null)

    L.tag("Tag1").d { "before main application" }
    DemoLogging.runTest()

    CanvasBasedWindow("Demo", canvasElementId = "ComposeTarget") {
        DemoApp(
            name = BuildKonfig.appName,
            platform = "WASM",
            setup = null, // no file logging on wasm
            ioContext = Dispatchers.Default,
            sendFeedback = null // only works on android currently
        )
    }
}