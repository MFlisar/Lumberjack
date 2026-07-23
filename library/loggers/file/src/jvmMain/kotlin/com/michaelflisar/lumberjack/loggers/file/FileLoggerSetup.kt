package com.michaelflisar.lumberjack.loggers.file

import java.io.File

fun FileLoggerSetup.Companion.getDefaultLogFolder(
    folderName: String = DEFAULT_LOG_FILE_FOLDER,
): String {
    return File(System.getProperty("user.dir"), folderName).absolutePath
}