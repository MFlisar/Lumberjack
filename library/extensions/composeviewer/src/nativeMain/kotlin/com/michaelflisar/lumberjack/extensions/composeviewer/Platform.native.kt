package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider

@Composable
internal actual fun ShowLumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    content: @Composable () -> Unit
) {
    if (visible.value) {
        Dialog(
            onDismissRequest = { visible.value = false }
        ) {
            content()
        }
    }
}

@Composable
internal actual fun LazyScrollContainer(state: LazyListState, content: LazyListScope.() -> Unit) {
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            state = state
        ) {
            content()
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(state)
        )
    }
}

internal actual fun getFeedbackImpl(): IFeedbackProvider {
    return FeedbackImpl()
}