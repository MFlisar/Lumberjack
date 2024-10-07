package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Companion.DEFAULT_SIZE_LIMIT_5MB
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Daily
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.FileSize
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import okio.Path.Companion.toPath

@OptIn(ExperimentalForeignApi::class)
fun FileLoggerSetup.Daily.Companion.create(
    fileBaseName: String = "log",
    fileExtension: String = "log",
    filesToKeep: Int = 1
) = Daily(
    NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    ).let {
        requireNotNull(it).path
    }!!.toPath().name,
    fileBaseName,
    fileExtension,
    filesToKeep
)

fun FileLoggerSetup.FileSize.Companion.create(
    fileBaseName: String = "log",
    fileExtension: String = "log",
    filesToKeep: Int = 1,
    maxFileSizeInBytes: Int = DEFAULT_SIZE_LIMIT_5MB
) = FileSize(
    NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    ).let {
        requireNotNull(it).path
    }.toPath(),
    fileBaseName,
    fileExtension,
    filesToKeep,
    maxFileSizeInBytes
)