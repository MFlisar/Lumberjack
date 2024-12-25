package com.michaelflisar.lumberjack.core

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import java.io.File

fun IFileLoggingSetup.getAllExistingLogFiles() = getAllExistingLogFilePaths().map {
    File(this.toString())
}
fun IFileLoggingSetup.getLatestLogFile() = getLatestLogFilePath()?.let{
    File(this.toString())
}