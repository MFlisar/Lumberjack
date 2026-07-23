package com.michaelflisar.lumberjack.loggers.file

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun FileLoggerSetup.Companion.getDefaultLogFolder(folderName: String): String {
    return NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    ).let {
        requireNotNull(it).path
    }!!
}