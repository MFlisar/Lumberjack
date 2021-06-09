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
    LumberjackViewer.show(context, fileLoggingSetup.getLatestLogFiles()!!)
}