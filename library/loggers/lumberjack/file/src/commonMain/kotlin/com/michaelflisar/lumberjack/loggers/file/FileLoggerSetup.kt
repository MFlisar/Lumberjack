package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.core.CommonIgnoredOnParcel
import com.michaelflisar.lumberjack.core.CommonParcelize
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import okio.FileSystem
import okio.Path
import okio.SYSTEM

sealed class FileLoggerSetup : IFileLoggingSetup {

    companion object {
        internal const val DEFAULT_LOG_FILE_FOLDER = "lumberjack"
        internal const val DEFAULT_SIZE_LIMIT_5MB = 5 * 1000 * 1000 // 5MB
    }

    abstract fun filePath(data: FileLogger.Event.Data): String
    abstract fun onLogged(scope: CoroutineScope)

    @CommonParcelize
    class Daily internal constructor(
        override val folder: String,
        override val fileBaseName: String,
        override val fileExtension: String,
        private val filesToKeep: Int,
        override var lastFileKey: String = "",
        override var lastFileKeyChanged: Boolean = false
    ) : BaseFileLoggerSetup() {

        companion object {
            internal fun create(
                folder: String,
                fileBaseName: String = "log",
                fileExtension: String = "log",
                filesToKeep: Int = 1
            ) = Daily(folder, fileBaseName, fileExtension, filesToKeep)
        }

        // "yyyy_MM_dd"
        @CommonIgnoredOnParcel
        private val timeFormatter = LocalDateTime.Format {
            year()
            char('_')
            monthNumber()
            char('_')
            dayOfMonth()
        }

        override fun getFileKey(data: FileLogger.Event.Data, lastPath: Path): String {
            val dateTime = DateTimeUtil.dateTime(data.time)
            val key = dateTime.format(timeFormatter)
            return key
        }

        override fun filterLogFilesToDelete(files: List<Path>): List<Path> {
            return files.drop(filesToKeep)
        }
    }

    @CommonParcelize
    class FileSize internal constructor(
        override val folder: String,
        override val fileBaseName: String,
        override val fileExtension: String,
        private val filesToKeep: Int,
        private val maxFileSizeInBytes: Int,
        override var lastFileKey: String = "",
        override var lastFileKeyChanged: Boolean = false
    ) : BaseFileLoggerSetup() {

        companion object {

            internal fun create(
                folder: String,
                fileBaseName: String = "log",
                fileExtension: String = "log",
                filesToKeep: Int = 1,
                maxFileSizeInBytes: Int = DEFAULT_SIZE_LIMIT_5MB
            ) = FileSize(folder, fileBaseName, fileExtension, filesToKeep, maxFileSizeInBytes)
        }

        @CommonIgnoredOnParcel
        private var fileIndex: Int? = null

        override fun getFileKey(data: FileLogger.Event.Data, lastPath: Path): String {
            if (fileIndex == null) {
                // we must find out what the highest existing log file index currently is
                fileIndex = getAllExistingLogFilePaths().lastOrNull()
                    ?.let { getKeyFromFile(it).toIntOrNull() } ?: 1
            }
            if (lastPath.name.isEmpty()) {
                return fileIndex.toString()
            }
            val bytes = lastPath.takeIf { FileSystem.SYSTEM.exists(it) }
                ?.let { FileSystem.SYSTEM.metadata(it).size }
            if ((bytes ?: 0L) >= maxFileSizeInBytes) {
                fileIndex = fileIndex!! + 1
            }
            return fileIndex.toString()
        }

        override fun filterLogFilesToDelete(files: List<Path>): List<Path> {
            return files.drop(filesToKeep)
        }
    }
}