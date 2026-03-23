package com.michaelflisar.lumberjack.core.classes

class FeedbackConfig(
    val receiver: String,
    val subject: String,
    val body: String = "",
    val bodyIsHtml: Boolean = false,
    val titleForChooser: String = "Send feedback with",
) {
    companion object {

        fun create(
            receiver: String,
            appName: String,
            appVersion: String,
            body: String = "",
            bodyIsHtml: Boolean = false,
            titleForChooser: String = "Send feedback with",
        ) = FeedbackConfig(
            receiver = receiver,
            subject = "Feedback for ${appName} (v${appVersion})",
            body = body,
            bodyIsHtml = bodyIsHtml,
            titleForChooser = titleForChooser,
        )
    }
}