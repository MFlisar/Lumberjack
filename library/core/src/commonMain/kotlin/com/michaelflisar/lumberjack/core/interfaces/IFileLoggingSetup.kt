package com.michaelflisar.lumberjack.core.interfaces

import com.michaelflisar.parcelize.Parcelable
import kotlinx.io.files.Path

interface IFileLoggingSetup : Parcelable {

    val fileConverter: IFileConverter

    fun getAllExistingLogFilePaths(): List<Path>
    fun getLatestLogFilePath(): Path?
    suspend fun clearLogFiles()


}