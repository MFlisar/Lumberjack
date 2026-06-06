package com.michaelflisar.demo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
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

    ComposeViewport(
        // mit container id geht es nicht --> wäre aber gut, dann würde ein Loader angezeigt werden, aktuell wird der nicht angezeigt...
        // viewportContainerId = wasmSetup.canvasElementId
    ) {
        DemoApp(
            name = BuildKonfig.appName,
            platform = "WASM",
            setup = null, // no file logging on wasm
            ioContext = Dispatchers.Default,
            sendFeedback = null // only works on android currently
        )
    }
}