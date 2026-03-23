package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Companion.DEFAULT_SIZE_LIMIT_5MB
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.Daily
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.FileSize
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup.SingleFile
import java.io.File

fun FileLoggerSetup.Daily.Companion.create(
    folder: File,
    fileBaseName: String = "log",
    fileExtension: String = "txt",
    filesToKeep: Int = 1
) = Daily(
    folder.absolutePath,
    fileBaseName,
    fileExtension,
    filesToKeep
)

fun FileLoggerSetup.FileSize.Companion.create(
    folder: File,
    fileBaseName: String = "log",
    fileExtension: String = "txt",
    filesToKeep: Int = 1,
    maxFileSizeInBytes: Int = DEFAULT_SIZE_LIMIT_5MB
) = FileSize(
    folder.absolutePath,
    fileBaseName,
    fileExtension,
    filesToKeep,
    maxFileSizeInBytes
)

fun FileLoggerSetup.SingleFile.Companion.create(
    folder: File,
    fileName: String = "log",
    fileExtension: String = "txt",
) = SingleFile(
    folder.absolutePath,
    fileName,
    fileExtension
)

fun FileLoggerSetup.SingleFile.Companion.create(
    file: File
) = SingleFile(
    file.parentFile.absolutePath,
    file.nameWithoutExtension,
    file.extension
)