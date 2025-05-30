package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

sealed class FileLoggerSetup : IFileLoggingSetup {

    companion object {
        internal const val DEFAULT_LOG_FILE_FOLDER = "lumberjack"
        internal const val DEFAULT_SIZE_LIMIT_5MB = 5 * 1000 * 1000 // 5MB
    }

    abstract fun filePath(data: FileLogger.Event.Data): String
    abstract fun onLogged(scope: CoroutineScope)

    @Parcelize
    class Daily internal constructor(
        override val folder: String,
        override val fileBaseName: String,
        override val fileExtension: String,
        private val filesToKeep: Int
    ) : BaseFileLoggerSetup() {

        companion object

        // "yyyy_MM_dd"
        @IgnoredOnParcel
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

    @Parcelize
    class FileSize internal constructor(
        override val folder: String,
        override val fileBaseName: String,
        override val fileExtension: String,
        private val filesToKeep: Int,
        private val maxFileSizeInBytes: Int
    ) : BaseFileLoggerSetup() {

        companion object

        @IgnoredOnParcel
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
            val bytes = lastPath.takeIf { SystemFileSystem.exists(it) }
                ?.let { SystemFileSystem.metadataOrNull(it)?.size }
            if ((bytes ?: 0L) >= maxFileSizeInBytes) {
                fileIndex = fileIndex!! + 1
            }
            return fileIndex.toString()
        }

        override fun filterLogFilesToDelete(files: List<Path>): List<Path> {
            return files.drop(filesToKeep)
        }
    }

    @Parcelize
    class SingleFile internal constructor(
        override val folder: String,
        private val fileName: String,
        override val fileExtension: String
    ) : BaseFileLoggerSetup() {

        companion object

        @IgnoredOnParcel
        override val fileBaseName: String = fileName

        override fun getFileKey(data: FileLogger.Event.Data, lastPath: Path): String {
            return "" // unused
        }

        override fun filterLogFilesToDelete(files: List<Path>): List<Path> {
            return emptyList()
        }
    }
}