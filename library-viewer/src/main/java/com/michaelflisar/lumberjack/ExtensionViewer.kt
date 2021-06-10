package com.michaelflisar.lumberjack

import android.content.Context
import android.content.Intent
import com.michaelflisar.lumberjack.interfaces.IDataExtractor
import com.michaelflisar.lumberjack.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.view.LumberjackViewerActivity

/*
 * convenient extension to show a log file
 */
fun L.showLog(
    context: Context,
    fileLoggingSetup: IFileLoggingSetup,
    dataExtractor: IDataExtractor = DefaultDataExtractor,
    title: String? = null
) {
    LumberjackViewerActivity.show(context, fileLoggingSetup, dataExtractor, title)
}

fun L.createViewerIntent(
    context: Context,
    fileLoggingSetup: IFileLoggingSetup,
    dataExtractor: IDataExtractor = DefaultDataExtractor,
    title: String? = null
): Intent = LumberjackViewerActivity.createIntent(context, fileLoggingSetup, dataExtractor, title)