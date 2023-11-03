package com.michaelflisar.lumberjack.extensions.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.michaelflisar.feedbackmanager.Feedback
import com.michaelflisar.feedbackmanager.FeedbackFile
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.CoreUtil
import java.io.File

sealed class NotificationClickHandler {

    /**
     * convenient class to simply send a feedback via clicking the notification via an intent chooser
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
    class SendFeedback(
        context: Context,
        val receiver: String,
        val subject: String = "Exception found in ${context.packageName}",
        val titleForChooser: String = "Send report with",
        val attachments: List<File> = emptyList()
    ) : NotificationClickHandler()

    /**
     * apply your custom notification click handler
     *
     * @param intent the intent to use when notification is clicked
     * @param apply optional adjustment function for the notification builder
     */
    class ClickIntent(
        val intent: Intent,
        val apply: ((builder: NotificationCompat.Builder) -> Unit)? = null
    ): NotificationClickHandler()

    /**
     * this will disable clicks on the notification => the notification will be there for information purposes only
     */
    data object None: NotificationClickHandler()
}


/**
 * convenient extension to simply show a notification for exceptions/infos/whatever that the user should report if possible
 *
 * @param context context start email chooser and retrieve default strings
 * @param notificationIcon the notification icon
 * @param notificationChannelId the notification channel id
 * @param notificationId the notification id
 * @param notificationTitle the notification title
 * @param notificationText the notification title
 * @param clickHandler the click handler for the notification
 */
fun L.showNotification(
    context: Context,
    notificationIcon: Int,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String = "Rare exception found",
    notificationText: String = "Please report this error by clicking this notification, thanks",
    clickHandler: NotificationClickHandler
) {
    when (clickHandler) {
        is NotificationClickHandler.SendFeedback -> {
            val feedback = Feedback(
                listOf(clickHandler.receiver),
                CoreUtil.getRealSubject(context, clickHandler.subject),
                attachments = clickHandler.attachments.map { FeedbackFile.DefaultName(it) }
            )
            feedback
                .startNotification(
                    context,
                    clickHandler.titleForChooser,
                    notificationTitle,
                    notificationText,
                    notificationIcon,
                    notificationChannelId,
                    notificationId
                )
        }
        is NotificationClickHandler.ClickIntent,
        is NotificationClickHandler.None -> {

            val pendingIntent = (clickHandler as? NotificationClickHandler.ClickIntent)?.let {
                val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE
                } else 0
                PendingIntent.getActivity(context, 0, it.intent, flag)
            }

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, notificationChannelId)
                    .setSmallIcon(notificationIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
            pendingIntent?.let { builder.setContentIntent(it) }
            (clickHandler as? NotificationClickHandler.ClickIntent)?.apply?.let { it(builder) }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())
        }
    }
}