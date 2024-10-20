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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import okio.Path

@Composable
internal actual fun ShowLumberjackDialog(
    visible: MutableState<Boolean>,
    title: String,
    content: @Composable () -> Unit
) {
    DialogWindow(
        visible = visible.value,
        title = title,
        onCloseRequest = { visible.value = false },
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            width = 800.dp,
            height = 600.dp
        )
    ) {
        content()
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

actual class FeedbackImpl actual constructor() {

    actual fun supported() = false

    @Composable
    actual fun Init() {
        // empty
    }

    actual fun sendFeedback(
        receiver: String,
        attachments: List<Path>
    ) {
        // empty
    }
}