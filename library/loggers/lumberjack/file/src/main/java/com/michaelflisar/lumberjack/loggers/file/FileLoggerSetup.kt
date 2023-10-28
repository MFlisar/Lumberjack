package com.michaelflisar.lumberjack.loggers.file

import android.content.Context
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

sealed class FileLoggerSetup : IFileLoggingSetup {

    abstract fun filePath(time: Long): String
    abstract fun onLogged(scope: CoroutineScope)

    @Parcelize
    data class Daily private constructor(
        val folder: File,
        val fileBaseName: String = "log",
        val fileExtension: String = "log",
        val filesToKeep: Int = 1,
        var lastFileNameKey: String = "",
        var keyChanged: Boolean = false
    ) : FileLoggerSetup() {

        constructor(
            context: Context,
            fileBaseName: String = "log",
            fileExtension: String = "log",
            filesToKeep: Int = 1
        ) : this(
            File(context.filesDir, "lumberjack"),
            fileBaseName,
            fileExtension,
            filesToKeep,
            "",
            false
        )

        constructor(
            folder: File,
            fileBaseName: String = "log",
            fileExtension: String = "log",
            filesToKeep: Int = 1
        ) : this(folder, fileBaseName, fileExtension, filesToKeep, "", false)

        @IgnoredOnParcel
        override val fileConverter = FileConverter

        @IgnoredOnParcel
        private val timeFormatter = SimpleDateFormat("yyyy_MM_dd")

        @IgnoredOnParcel
        private val date = Date()

        override fun filePath(time: Long): String {
            date.time = time
            val key = timeFormatter.format(date)
            val path = "${folder.path}/${fileBaseName}_${key}.$fileExtension"
            if (key != lastFileNameKey) {
                lastFileNameKey = key
                keyChanged = true
            }
            return path
        }

        override fun onLogged(scope: CoroutineScope) {
            if (keyChanged) {
                keyChanged = false
                scope.launch {
                    clearLogFiles(false)
                }
            }
        }

        override suspend fun clearLogFiles() {
            clearLogFiles(true)
        }

        override fun getAllExistingLogFiles(): List<File> {
            return folder.listFiles()?.filter {
                it.name.startsWith(fileBaseName)
            }?.sortedByDescending { it.name } ?: emptyList()
        }

        override fun getLatestLogFiles(): File? {
            return getAllExistingLogFiles().firstOrNull()
        }

        private suspend fun clearLogFiles(all: Boolean) {
            withContext(Dispatchers.IO) {
                val files = getAllExistingLogFiles()
                val filesToDelete = files.drop(if (all) 0 else filesToKeep)
                LumberjackLogger.loggers()
                    .filterIsInstance<FileLogger>()
                    .filter { it.setup == this }
                    .forEach {
                        it.onLogFilesWillBeDeleted(filesToDelete)
                    }
                filesToDelete.forEach {
                    it.delete()
                }
            }
        }
    }
    /*
        class FileSize(
            val fileName: String,
            val folder: File,
            val maxFileSizeInBytes: Int = 5 * 1000 * 1000 // 5MB
        ) : FileLoggerSetup() {
            override fun provideFile() = File(folder, fileName)
        }*/
}