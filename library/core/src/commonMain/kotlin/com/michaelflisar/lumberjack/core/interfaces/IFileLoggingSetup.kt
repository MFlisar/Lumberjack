package com.michaelflisar.lumberjack.core.interfaces

import dev.icerock.moko.parcelize.Parcelable
import okio.Path

interface IFileLoggingSetup : Parcelable {

    val fileConverter: IFileConverter

    fun getAllExistingLogFilePaths(): List<Path>
    fun getLatestLogFilePath(): Path?
    suspend fun clearLogFiles()


}