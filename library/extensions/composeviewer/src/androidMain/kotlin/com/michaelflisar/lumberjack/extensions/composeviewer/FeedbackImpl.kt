package com.michaelflisar.lumberjack.extensions.composeviewer

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.extensions.composeviewer.internal.IFeedbackProvider
import com.michaelflisar.lumberjack.extensions.feedback.sendFeedback
import kotlinx.io.files.Path
import java.io.File

internal class FeedbackImpl : IFeedbackProvider {

    private lateinit var context: Context

    override fun supported() = false

    @Composable
    override fun Init() {
        context = LocalContext.current
    }

    override fun sendFeedback(
        receiver: String,
        attachments: List<Path>
    ) {
        L.sendFeedback(
            context = context,
            receiver = receiver,
            attachments = attachments.map { File(it.toString()) }
        )
    }
}