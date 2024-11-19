package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.runtime.Composable
import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider
import okio.Path

internal class FeedbackImpl : IFeedbackProvider {

    override fun supported() = false

    @Composable
    override fun Init() {
        // empty
    }

    override fun sendFeedback(
        receiver: String,
        attachments: List<Path>
    ) {
        // empty
    }
}