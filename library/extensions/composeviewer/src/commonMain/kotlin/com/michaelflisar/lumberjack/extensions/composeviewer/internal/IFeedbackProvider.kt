package com.michaelflisar.lumberjack.extensions.composeviewer.internal

import androidx.compose.runtime.Composable
import kotlinx.io.files.Path

internal interface IFeedbackProvider {

    fun supported(): Boolean

    @Composable
    fun Init()

    fun sendFeedback(receiver: String, attachments: List<Path>)
}