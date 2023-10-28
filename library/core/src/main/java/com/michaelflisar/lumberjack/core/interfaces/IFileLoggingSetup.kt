package com.michaelflisar.lumberjack.core.interfaces

import android.os.Parcelable
import com.michaelflisar.lumberjack.core.classes.Level
import java.io.File

interface IFileLoggingSetup : Parcelable {

    val fileConverter: IFileConverter

    fun getAllExistingLogFiles(): List<File>
    fun getLatestLogFiles(): File?
    suspend fun clearLogFiles()


}