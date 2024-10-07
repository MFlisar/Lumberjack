package com.michaelflisar.lumberjack.core.interfaces

import com.michaelflisar.lumberjack.core.CommonParcelable
import okio.Path

interface IFileLoggingSetup : CommonParcelable {

    val fileConverter: IFileConverter

    fun getAllExistingLogFilePaths(): List<Path>
    fun getLatestLogFilePath(): Path?
    suspend fun clearLogFiles()


}