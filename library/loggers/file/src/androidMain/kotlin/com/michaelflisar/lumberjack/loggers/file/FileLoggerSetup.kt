package com.michaelflisar.lumberjack.loggers.file

import android.content.Context
import java.io.File

fun FileLoggerSetup.Companion.getDefaultLogFolder(
    context: Context,
    folderName: String = DEFAULT_LOG_FILE_FOLDER,
): String {
    return File(context.filesDir, folderName).absolutePath
}