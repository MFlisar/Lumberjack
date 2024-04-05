package com.michaelflisar.lumberjack.loggers.file

import android.annotation.SuppressLint
import android.content.Context
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.min

sealed class FileLoggerSetup : IFileLoggingSetup {

    companion object {
        private const val DEFAULT_LOG_FILE_FOLDER = "lumberjack"
        private const val DEFAULT_SIZE_LIMIT_5MB = 5 * 1000 * 1000 // 5MB
    }

    abstract fun filePath(data: FileLogger.Event.Data): String
    abstract fun onLogged(scope: CoroutineScope)

    @Parcelize
    class Daily private constructor(
        override val folder: File,
        override val fileBaseName: String,
        override val fileExtension: String,
        private val filesToKeep: Int,
        override var lastFileKey: String = "",
        override var lastFileKeyChanged: Boolean = false
    ) : BaseFileLoggerSetup() {

        companion object {
            fun create(
                context: Context,
                fileBaseName: String = "log",
                fileExtension: String = "log",
                filesToKeep: Int = 1
            ) = Daily(
                File(context.filesDir, DEFAULT_LOG_FILE_FOLDER),
                fileBaseName,
                fileExtension,
                filesToKeep
            )

            fun create(
                folder: File,
                fileBaseName: String = "log",
                fileExtension: String = "log",
                filesToKeep: Int = 1
            ) = Daily(folder, fileBaseName, fileExtension, filesToKeep)
        }

        @SuppressLint("SimpleDateFormat")
        @IgnoredOnParcel
        private val timeFormatter = SimpleDateFormat("yyyy_MM_dd")

        @IgnoredOnParcel
        private val date = Date()

        override fun getFileKey(data: FileLogger.Event.Data, lastPath: String): String {
            date.time = data.time
            val key = timeFormatter.format(date)
            return key
        }

        override fun filterLogFilesToDelete(files: List<File>): List<File> {
            return files.drop(filesToKeep)
        }
    }

    @Parcelize
    class FileSize private constructor(
        override val folder: File,
        override val fileBaseName: String,
        override val fileExtension: String,
        private val filesToKeep: Int,
        private val maxFileSizeInBytes: Int,
        override var lastFileKey: String = "",
        override var lastFileKeyChanged: Boolean = false
    ) : BaseFileLoggerSetup() {

        companion object {

            fun create(
                context: Context,
                fileBaseName: String = "log",
                fileExtension: String = "log",
                filesToKeep: Int = 1,
                maxFileSizeInBytes: Int = DEFAULT_SIZE_LIMIT_5MB
            ) = FileSize(
                File(context.filesDir, DEFAULT_LOG_FILE_FOLDER),
                fileBaseName,
                fileExtension,
                filesToKeep,
                maxFileSizeInBytes
            )

            fun create(
                folder: File,
                fileBaseName: String = "log",
                fileExtension: String = "log",
                filesToKeep: Int = 1,
                maxFileSizeInBytes: Int = DEFAULT_SIZE_LIMIT_5MB
            ) = FileSize(folder, fileBaseName, fileExtension, filesToKeep, maxFileSizeInBytes)
        }

        @IgnoredOnParcel
        private var fileIndex: Int? = null

        override fun getFileKey(data: FileLogger.Event.Data, lastPath: String): String {
            if (fileIndex == null) {
                // we must find out what the highest existing log file index currently is
                fileIndex =
                    getAllExistingLogFiles().lastOrNull()?.let { getKeyFromFile(it).toIntOrNull() }
                        ?: 1
            }
            if (lastPath.isEmpty()) {
                return fileIndex.toString()
            }
            val bytes = File(lastPath).takeIf { it.exists() }?.length()
            if ((bytes ?: 0L) >= maxFileSizeInBytes) {
                fileIndex = fileIndex!! + 1
            }
            return fileIndex.toString()
        }

        override fun filterLogFilesToDelete(files: List<File>): List<File> {
            return files.drop(filesToKeep)
        }
    }
}