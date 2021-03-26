package com.michaelflisar.lumberjack

import android.content.Context
import android.content.pm.PackageManager
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