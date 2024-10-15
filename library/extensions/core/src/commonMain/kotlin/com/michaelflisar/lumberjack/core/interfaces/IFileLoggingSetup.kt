package com.michaelflisar.lumberjack.core.interfaces

import com.michaelflisar.lumberjack.core.Parcelable
import java.io.File

interface IFileLoggingSetup : Parcelable {

    val fileConverter: IFileConverter

    fun getAllExistingLogFiles(): List<File>
    fun getLatestLogFile(): File?
    suspend fun clearLogFiles()

}