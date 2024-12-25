package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider

@Composable
internal expect fun ShowLumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    content: @Composable () -> Unit
)

@Composable
internal expect fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit)

internal expect fun getFeedbackImpl(): IFeedbackProvider