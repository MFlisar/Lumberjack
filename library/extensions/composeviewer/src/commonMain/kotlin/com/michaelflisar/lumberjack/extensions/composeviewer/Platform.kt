package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import okio.Path

@Composable
expect fun ShowLumberjackDialog(visible: MutableState<Boolean>, content: @Composable () -> Unit)

expect class FeedbackImpl() {

    fun supported() : Boolean

    @Composable
    fun Init()

    fun sendFeedback(receiver: String, attachments: List<Path>)
}