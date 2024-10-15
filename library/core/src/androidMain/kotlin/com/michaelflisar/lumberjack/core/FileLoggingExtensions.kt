package com.michaelflisar.lumberjack.core

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup

fun IFileLoggingSetup.getAllExistingLogFiles() = getAllExistingLogFilePaths().map { it.toFile() }
fun IFileLoggingSetup.getLatestLogFile() = getLatestLogFilePath()?.toFile()