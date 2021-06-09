package com.michaelflisar.lumberjack

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.michaelflisar.feedbackmanager.FeedbackBuilder
import com.michaelflisar.lumberjack.core.CoreUtil
import com.michaelflisar.lumberjack.interfaces.ILumberjackViewActivityProvider
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
    val builder = FeedbackBuilder.create()
        .withSubject(CoreUtil.getRealSubject(context, subject))
        .addReceiver(receiver)
    logFile?.let { builder.addFile(it) }
    filesToAppend.forEach {
        builder.addFile(it)
    }

    builder
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
    notificationIcon: Int
) {
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setContentText(notificationText)

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}

/*
 * convenient extension to simply show a notification to the user or for debugging infos
 *
 * on click this will open the log file in an activity
 */
fun L.showInfoNotification(
    context: Context,
    logFile: File?,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String,
    notificationText: String,
    notificationIcon: Int,
    lumberjackViewerActivityProvider: ILumberjackViewActivityProvider
) {
    val pendingIntent: PendingIntent? = logFile?.let {
        val clickIntent = lumberjackViewerActivityProvider.createIntent(context, logFile)
        PendingIntent.getActivity(context, 0, clickIntent, 0)
    }

    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setContentText(notificationText)
    pendingIntent?.let { builder.setContentIntent(it) }

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}