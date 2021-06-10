package com.michaelflisar.lumberjack

import android.content.Context
import com.michaelflisar.lumberjack.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.view.LumberjackViewer

/*
 * convenient extension to show a log file
 */
fun L.showLog(
    context: Context,
    fileLoggingSetup: IFileLoggingSetup
) {
    LumberjackViewer.show(context, fileLoggingSetup)
}