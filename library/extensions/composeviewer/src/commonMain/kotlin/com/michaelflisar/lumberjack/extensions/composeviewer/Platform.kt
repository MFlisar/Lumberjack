package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import okio.Path

@Composable
internal expect fun ShowLumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    content: @Composable () -> Unit
)

@Composable
internal expect fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit)

expect class FeedbackImpl() {

    fun supported(): Boolean

    @Composable
    fun Init()

    fun sendFeedback(receiver: String, attachments: List<Path>)
}