package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.kmp.platformcontext.PlatformApplicationContext
import java.io.File

actual fun FileLoggerSetup.Companion.getDefaultLogFolder(folderName: String): String {
    return File(PlatformApplicationContext.filesDir, folderName).absolutePath
}