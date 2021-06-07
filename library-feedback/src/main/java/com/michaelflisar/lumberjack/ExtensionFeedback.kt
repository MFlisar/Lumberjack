package com.michaelflisar.lumberjack

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import com.michaelflisar.feedbackmanager.FeedbackBuilder
import java.io.File


/*
 * convenient extension to simply send a feedback via email with an intent chooser
 *
 * - app version will be appended to the subject automatically
 * - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 */
fun L.sendFeedback(
    context: Context,
    fileLoggingSetup: FileLoggingSetup,
    receiver: String,
    appendLogFile: Boolean = true,
    subject: String = "Feedback for ${context.packageName}",
    titleForChooser: String = "Send feedback with",
    filesToAppend: List<File> = emptyList()
) {
    val builder = FeedbackBuilder.create()
        .withSubject(getRealSubject(context, subject))
        .addReceiver(receiver)
    if (appendLogFile) {
        fileLoggingSetup.getLatestLogFiles()?.let {
            builder.addFile(it)
        }
    }
    filesToAppend.forEach {
        builder.addFile(it)
    }
    builder.startEmailChooser(context, titleForChooser)
}

/*
 * convenient extension to simply show a notification for exceptions/infos/whatever that the user should report if possible
 *
 * - app version will be appended to the subject automatically
 * - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 */
fun L.showCrashNotification(
    context: Context,
    fileLoggingSetup: FileLoggingSetup,
    receiver: String,
    appIcon: Int,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String = "Rare exception found",
    notificationText: String = "Please report this error by clicking this notification, thanks",
    subject: String = "Exception found in ${context.packageName}",
    appendLogFile: Boolean = true,
    titleForChooser: String = "Send report with",
    filesToAppend: List<File> = emptyList()
) {
    val builder = FeedbackBuilder.create()
        .withSubject(getRealSubject(context, subject))
        .addReceiver(receiver)
    if (appendLogFile) {
        fileLoggingSetup.getLatestLogFiles()?.let {
            builder.addFile(it)
        }
    }
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
 * convenient extension to simply show a notification for exceptions/infos/whatever that the user should report if possible
 *
 * - app version will be appended to the subject automatically
 * - file should be local and accessible files - they will be exposed via a ContentProvider so that the email client can access the file
 */
fun L.showInfoNotification(
    context: Context,
    notificationChannelId: String,
    notificationId: Int,
    notificationTitle: String,
    notificationText: String,
    notificationIcon: Int,
) {
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(notificationTitle)
        .setContentText(notificationText)
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}

// -------------------
// helper functions
// -------------------

private fun getRealSubject(context: Context, subject: String): String {
   return context.getAppVersionName()?.let { "$subject (v$it)" } ?: subject
}

private fun Context.getAppVersionName(): String? {
    return try {
        val info = packageManager.getPackageInfo(packageName, 0)
        info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}