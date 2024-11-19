package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider

@Composable
internal actual fun ShowLumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    content: @Composable () -> Unit
) {
    FullScreenDialog(visible, content = content)
}

@Composable
internal actual fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit) {
    LazyColumn(state = state) {
        content()
    }
}

internal actual fun getFeedbackImpl(): IFeedbackProvider {
    return FeedbackImpl()
}