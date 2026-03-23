package com.michaelflisar.lumberjack.extensions.composeviewer

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import kotlinx.io.files.Path

internal class FeedbackImpl : IFeedbackProvider {

    override fun supported() = false

    override fun sendFeedback(
        config: FeedbackConfig,
        attachments: List<Path>,
    ) {
        L.sendFeedback(
            config = config,
            attachments = attachments
        )
    }
}