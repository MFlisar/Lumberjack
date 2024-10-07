package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import okio.Path

@Composable
actual fun ShowLumberjackDialog(visible: MutableState<Boolean>, content: @Composable () -> Unit) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = true),
        onDismissRequest = { visible.value = false }
    ) {
        content()
    }
}

actual class FeedbackImpl actual constructor() {

    actual fun supported() = false

    @Composable
    actual fun Init() {
        // empty
    }

    actual fun sendFeedback(
        receiver: String,
        attachments: List<Path>
    ) {
        // empty
    }
}