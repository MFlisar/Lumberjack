package com.michaelflisar.lumberjack

import android.content.Context
import com.michaelflisar.feedbackmanager.FeedbackBuilder
import com.michaelflisar.lumberjack.core.CoreUtil
import java.io.File

/*
 * convenient extension to simply send a feedback via email with an intent chooser
 *
 * - app version will be appended to the subject automatically
 * - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 */
fun L.sendFeedback(
    context: Context,
    logFile: File?,
    receiver: String,
    subject: String = "Feedback for ${context.packageName}",
    titleForChooser: String = "Send feedback with",
    filesToAppend: List<File> = emptyList()
) {
    val builder = FeedbackBuilder.create()
        .withSubject(CoreUtil.getRealSubject(context, subject))
        .addReceiver(receiver)
    logFile?.let { builder.addFile(it) }
    filesToAppend.forEach {
        builder.addFile(it)
    }
    builder.startEmailChooser(context, titleForChooser)
}