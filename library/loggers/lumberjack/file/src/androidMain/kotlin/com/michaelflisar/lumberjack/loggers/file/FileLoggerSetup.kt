package com.michaelflisar.lumberjack.loggers.file

import android.content.Context
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Companion.DEFAULT_LOG_FILE_FOLDER
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Companion.DEFAULT_SIZE_LIMIT_5MB
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Daily
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.FileSize
import okio.Path.Companion.toOkioPath
import java.io.File

fun FileLoggerSetup.Daily.Companion.create(
    context: Context,
    fileBaseName: String = "log",
    fileExtension: String = "log",
    filesToKeep: Int = 1
) = Daily(
    File(context.filesDir, DEFAULT_LOG_FILE_FOLDER).absolutePath,
    fileBaseName,
    fileExtension,
    filesToKeep
)

fun FileLoggerSetup.FileSize.Companion.create(
    context: Context,
    fileBaseName: String = "log",
    fileExtension: String = "log",
    filesToKeep: Int = 1,
    maxFileSizeInBytes: Int = DEFAULT_SIZE_LIMIT_5MB
) = FileSize(
    File(context.filesDir, DEFAULT_LOG_FILE_FOLDER).absolutePath,
    fileBaseName,
    fileExtension,
    filesToKeep,
    maxFileSizeInBytes
)