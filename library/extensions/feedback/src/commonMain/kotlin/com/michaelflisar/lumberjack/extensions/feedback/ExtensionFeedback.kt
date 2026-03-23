package com.michaelflisar.lumberjack.extensions.feedback

import com.michaelflisar.kmpmail.Mail
import com.michaelflisar.kmpmail.MailAttachmentFile
import com.michaelflisar.kmpmail.startEmailChooser
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.FeedbackConfig
import kotlinx.io.files.Path

/**
 * convenient extension to simply send a feedback via email with an intent chooser
 *
 *  - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 *
 * @param config [com.michaelflisar.lumberjack.core.classes.FeedbackConfig] for the feedback mail
 * @param attachments files that should be appended to the mail
 */
fun L.sendFeedback(
    config: FeedbackConfig,
    attachments: List<Path> = emptyList(),
) {
    val mail = Mail(
        receivers = listOf(config.receiver),
        subject = config.subject,
        body = config.body,
        bodyIsHtml = config.bodyIsHtml,
        attachments = attachments.map { MailAttachmentFile(it) }
    )
    mail.startEmailChooser(config.titleForChooser)
}