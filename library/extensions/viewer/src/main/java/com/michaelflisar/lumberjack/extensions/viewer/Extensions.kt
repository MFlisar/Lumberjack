package com.michaelflisar.lumberjack.extensions.viewer

import android.content.Context
import android.content.Intent
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.extensions.viewer.view.LumberjackViewerActivity

/*
 * convenient extension to show a log file
 */
fun L.showLog(
    context: Context,
    fileLoggingSetup: IFileLoggingSetup,
    receiver: String? = null,
    title: String? = null,
    theme: Int? = null
) {
    LumberjackViewerActivity.show(context, fileLoggingSetup, receiver, title, theme)
}

fun L.createViewerIntent(
    context: Context,
    fileLoggingSetup: IFileLoggingSetup,
    receiver: String? = null,
    title: String? = null,
    theme: Int? = null
): Intent = LumberjackViewerActivity.createIntent(
    context,
    fileLoggingSetup,
    receiver,
    title,
    theme
)