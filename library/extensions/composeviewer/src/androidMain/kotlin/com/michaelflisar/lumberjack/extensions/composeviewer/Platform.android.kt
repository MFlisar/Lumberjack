package com.michaelflisar.lumberjack.extensions.composeviewer

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import okio.Path

@Composable
internal actual fun ShowLumberjackDialog(visible: MutableState<Boolean>, content: @Composable () -> Unit) {
    FullScreenDialog(visible, content = content)
}

actual class FeedbackImpl actual constructor() {

    private lateinit var context: Context

    actual fun supported() = false

    @Composable
    actual fun Init() {
        context = LocalContext.current
    }

    actual fun sendFeedback(
        receiver: String,
        attachments: List<Path>
    ) {
        L.sendFeedback(
            context = context,
            receiver = receiver,
            attachments = attachments.map { it.toFile() }
        )
    }
}