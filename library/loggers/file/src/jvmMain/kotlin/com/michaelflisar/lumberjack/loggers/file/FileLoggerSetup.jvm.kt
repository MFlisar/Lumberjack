package com.michaelflisar.lumberjack.loggers.file

import java.io.File

actual fun FileLoggerSetup.Companion.getDefaultLogFolder(folderName: String): String {
    return File(System.getProperty("user.dir"), folderName).absolutePath
}