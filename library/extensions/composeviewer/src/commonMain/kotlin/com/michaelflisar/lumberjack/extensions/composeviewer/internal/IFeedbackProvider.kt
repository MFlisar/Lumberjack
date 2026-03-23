package com.michaelflisar.lumberjack.extensions.composeviewer.internal

import androidx.compose.runtime.Composable
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import kotlinx.io.files.Path

internal interface IFeedbackProvider {

    fun supported(): Boolean

    @Composable
    fun Init()

    fun sendFeedback(config: FeedbackConfig, attachments: List<Path>)
}