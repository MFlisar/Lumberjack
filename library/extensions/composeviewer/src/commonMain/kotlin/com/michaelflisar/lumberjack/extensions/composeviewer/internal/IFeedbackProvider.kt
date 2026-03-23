package com.michaelflisar.lumberjack.extensions.composeviewer.internal

import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import kotlinx.io.files.Path

internal interface IFeedbackProvider {

    fun supported(): Boolean

    fun sendFeedback(config: FeedbackConfig, attachments: List<Path>)
}