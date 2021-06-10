package com.michaelflisar.lumberjack

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.michaelflisar.feedbackmanager.Feedback
import com.michaelflisar.feedbackmanager.FeedbackFile
import com.michaelflisar.lumberjack.core.CoreUtil
import java.io.File

/*
 * convenient extension to simply show a notification for exceptions/infos/whatever that the user should report if possible
 *
 * - app version will be appended to the subject automatically
 * - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 */
fun L.showCrashNotification(
    context: Context,
    logFile: File?,
    receiver: String,
    appIcon: Int,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String = "Rare exception found",
    notificationText: String = "Please report this error by clicking this notification, thanks",
    subject: String = "Exception found in ${context.packageName}",
    titleForChooser: String = "Send report with",
    filesToAppend: List<File> = emptyList()
) {
    val allFiles = filesToAppend.toMutableList()
    logFile?.let { allFiles.add(0, it) }
    val feedback = Feedback(
        listOf(receiver),
        CoreUtil.getRealSubject(context, subject),
        attachments = allFiles.map { FeedbackFile.DefaultName(it) }
    )

    feedback
        .startNotification(
            context,
            titleForChooser,
            notificationTitle,
            notificationText,
            appIcon,
            notificationChannelId,
            notificationId
        )
}

/*
 * convenient extension to simply show a notification to the user or for debugging infos
 */
fun L.showInfoNotification(
    context: Context,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String,
    notificationText: String,
    notificationIcon: Int,
    clickIntent: Intent? = null,
    apply: ((builder: NotificationCompat.Builder) -> Unit)? = null
) {
    val pendingIntent: PendingIntent? = clickIntent?.let {
        PendingIntent.getActivity(context, 0, it, 0)
    }

    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setContentText(notificationText)
    pendingIntent?.let { builder.setContentIntent(it) }
    apply?.let { it(builder) }

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}