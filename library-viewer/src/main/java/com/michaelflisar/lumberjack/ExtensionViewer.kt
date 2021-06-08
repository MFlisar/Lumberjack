package com.michaelflisar.lumberjack

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import com.michaelflisar.feedbackmanager.FeedbackBuilder
import java.io.File

/*
 * convenient extension to show a log file
 */
fun L.showLog(
    context: Context,
    fileLoggingSetup: FileLoggingSetup
) {
    LumberjackViewerActivity.show(context, fileLoggingSetup.getLatestLogFiles()!!)
}

/*
 * convenient extension to simply show a notification to the user or for debugging infos
 *
 * on click this will open the log file in an activity
 */
fun L.showInfoNotification(
    context: Context,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String,
    notificationText: String,
    notificationIcon: Int,
    fileLoggingSetup: FileLoggingSetup
    ) {

    val clickIntent = LumberjackViewerActivity.createIntent(context, fileLoggingSetup.getLatestLogFiles()!!)
    val pendingClickIntent = PendingIntent.getActivity(context, 0, clickIntent, 0)

    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setContentText(notificationText)
        .setContentIntent(pendingClickIntent)



    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}