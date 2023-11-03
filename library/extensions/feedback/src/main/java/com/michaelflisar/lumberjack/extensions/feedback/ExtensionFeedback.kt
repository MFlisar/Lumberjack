package com.michaelflisar.lumberjack.extensions.feedback

import android.content.Context
import com.michaelflisar.feedbackmanager.Feedback
import com.michaelflisar.feedbackmanager.FeedbackFile
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.CoreUtil
import java.io.File

/**
 * convenient extension to simply send a feedback via email with an intent chooser
 *
 *  - app version will be appended to the subject automatically
 *  - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 *
 * @param context context for the email chooser and to retrieve default strings
 * @param receiver the receiver email of the feedback
 * @param subject the subject of the mail
 * @param titleForChooser the title of the email app chooser
 * @param attachments files that should be appended to the mail
 */
fun L.sendFeedback(
    context: Context,
    receiver: String,
    subject: String = "Feedback for ${context.packageName}",
    titleForChooser: String = "Send feedback with",
    attachments: List<File> = emptyList()
) {
    val feedback = Feedback(
        listOf(receiver),
        CoreUtil.getRealSubject(context, subject),
        attachments = attachments.map { FeedbackFile.DefaultName(it) }
    )
    feedback.startEmailChooser(context, titleForChooser)
}