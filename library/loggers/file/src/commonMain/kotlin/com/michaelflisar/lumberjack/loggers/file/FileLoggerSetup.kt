package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelize
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.time.Clock

expect fun FileLoggerSetup.Companion.getDefaultLogFolder(folderName: String = DEFAULT_LOG_FILE_FOLDER): String

sealed class FileLoggerSetup : BaseFileLoggerSetup() {

    companion object {
        internal const val DEFAULT_LOG_FILE_FOLDER = "lumberjack"
        const val DEFAULT_FILE_BASE_NAME: String = "log"
        const val DEFAULT_FILE_EXTENSION: String = "txt"
        const val DEFAULT_FILES_TO_KEEP: Int = 5
        const val DEFAULT_MAX_FILE_SIZE_IN_BYTES: Int = 5 * 1000 * 1000 // 5MB
    }

    /**
     * Creates one log file per day.
     *
     * When the date changes, logging automatically continues in a new file.
     * Keeps only the newest [filesToKeep] daily log files and removes older ones.
     *
     * Defaults:
     * - fileBaseName = "log"
     * - fileExtension = "txt"
     * - filesToKeep = 5
     */
    @Parcelize
    class Daily(
        override val folder: String = getDefaultLogFolder(),
        override val fileBaseName: String = DEFAULT_FILE_BASE_NAME,
        override val fileExtension: String = DEFAULT_FILE_EXTENSION,
        private val filesToKeep: Int = DEFAULT_FILES_TO_KEEP,
    ) : FileLoggerSetup() {

        companion object {

            // "yyyy_MM_dd"
            private val timeFormatter = LocalDateTime.Format {
                year()
                char('_')
                monthNumber()
                char('_')
                day()
            }
        }

        override fun getRollOverType(
            currentPath: Path?,
            event: FileLogger.Event.Log,
        ): RollOverType {
            if (currentPath == null)
                return RollOverType.None
            if (currentPath == getActiveLogFilePath(event))
                return RollOverType.None
            return RollOverType.SwitchFile
        }

        override fun getActiveLogFilePath(
            event: FileLogger.Event.Log,
        ): Path {
            val key = DateTimeUtil
                .dateTime(event.time)
                .format(timeFormatter)
            return Path("$folder/${fileBaseName}_$key.$fileExtension")
        }

        override fun getAllLogFiles(sorted: Boolean): List<Path> {
            val folder = Path(folder)
            if (!SystemFileSystem.exists(folder))
                return emptyList()
            val files = SystemFileSystem.list(folder)
                .filter {
                    it.name.endsWith(".$fileExtension") &&
                            it.name.startsWith("${fileBaseName}_")
                }
                .toList()
            // works because format can be sorted by date as simple string
            return files
                .sortedByDescending { it.name }
        }

        override fun filterFilesToDelete(files: List<Path>): List<Path> {
            return files.drop(filesToKeep)
        }
    }

    /**
     * Writes all logs to a single active file.
     *
     * When the file reaches [maxFileSizeInBytes], it is archived and a new
     * active file is created. Keeps only the newest [filesToKeep] log files
     * (including the active file).
     *
     * Defaults:
     * - fileBaseName = "log"
     * - fileExtension = "txt"
     * - filesToKeep = 5
     * - maxFileSizeInBytes = 5 MB
     */
    @Parcelize
    class FileSize(
        override val folder: String = getDefaultLogFolder(),
        override val fileBaseName: String = DEFAULT_FILE_BASE_NAME,
        override val fileExtension: String = DEFAULT_FILE_EXTENSION,
        private val filesToKeep: Int = DEFAULT_FILES_TO_KEEP,
        private val maxFileSizeInBytes: Int = DEFAULT_MAX_FILE_SIZE_IN_BYTES,
    ) : FileLoggerSetup() {

        companion object {
            private val archiveFormatter = LocalDateTime.Format {
                year()
                monthNumber()
                day()
                char('_')
                hour()
                minute()
                second()
                char('_')
                secondFraction(3)
            }
        }

        override fun getActiveLogFilePath(
            event: FileLogger.Event.Log,
        ): Path {
            return Path("$folder/$fileBaseName.$fileExtension")
        }

        override fun getRollOverType(
            currentPath: Path?,
            event: FileLogger.Event.Log,
        ): RollOverType {

            if (currentPath == null)
                return RollOverType.None

            val size =
                SystemFileSystem
                    .metadataOrNull(currentPath)
                    ?.size
                    ?: return RollOverType.None

            if (size < maxFileSizeInBytes)
                return RollOverType.None

            return RollOverType.ArchiveFile(
                archivePath = createArchivePath()
            )
        }

        override fun getAllLogFiles(sorted: Boolean): List<Path> {

            val folderPath = Path(folder)

            if (!SystemFileSystem.exists(folderPath))
                return emptyList()

            val files = SystemFileSystem
                .list(folderPath)
                .filter {
                    it.name.endsWith(".$fileExtension") &&
                            it.name.startsWith(fileBaseName)
                }
                .toList()

            return files
                .sortedByDescending { it.name }
        }

        override fun filterFilesToDelete(
            files: List<Path>,
        ): List<Path> {

            val archivedFiles =
                files.filter {
                    it.name.startsWith("${fileBaseName}_archived_")
                }

            return archivedFiles
                .drop(filesToKeep - 1)
        }

        private fun createArchivePath(): Path {
            val key = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(archiveFormatter)
            var index = 0
            while (true) {
                val suffix = if (index == 0) {
                    ""
                } else {
                    "_${index.toString().padStart(6, '0')}"
                }
                val path = Path("$folder/${fileBaseName}_archived_${key}$suffix.$fileExtension")
                if (!SystemFileSystem.exists(path)) {
                    return path
                }
                index++
            }
        }
    }

    /**
     * Writes all logs to a single file.
     *
     * When the file reaches [maxFileSizeInBytes], it is trimmed instead of
     * creating archive files. Approximately the newest [trimFactor] portion
     * of the file is kept.
     *
     * Defaults:
     * - fileName = "log"
     * - fileExtension = "txt"
     * - maxFileSizeInBytes = 5 MB
     * - trimFactor = 0.5 (keep roughly 50%)
     */
    @Parcelize
    class SingleFile(
        override val folder: String = getDefaultLogFolder(),
        val fileName: String = DEFAULT_FILE_BASE_NAME,
        override val fileExtension: String = DEFAULT_FILE_EXTENSION,
        private val maxFileSizeInBytes: Int = DEFAULT_MAX_FILE_SIZE_IN_BYTES,
        private val trimFactor: Float = 0.5f,
    ) : FileLoggerSetup() {

        companion object {}

        init {
            require(trimFactor > 0f && trimFactor < 1f) {
                "trimFactor must be between 0 and 1"
            }

            require(maxFileSizeInBytes > 0) {
                "maxFileSizeInBytes must be greater than 0"
            }
        }

        @IgnoredOnParcel
        override val fileBaseName: String = fileName

        @IgnoredOnParcel
        val path = Path("$folder/$fileName.$fileExtension")

        override fun getRollOverType(
            currentPath: Path?,
            event: FileLogger.Event.Log,
        ): RollOverType {
            val size = SystemFileSystem
                .metadataOrNull(path)
                ?.size
                ?: return RollOverType.None
            return if (size >= maxFileSizeInBytes) {
                RollOverType.TrimFile(trimFactor)
            } else {
                RollOverType.None
            }
        }

        override fun getActiveLogFilePath(
            event: FileLogger.Event.Log,
        ): Path {
            return path
        }

        override fun getAllLogFiles(sorted: Boolean): List<Path> {
            return if (SystemFileSystem.exists(path)) {
                listOf(path)
            } else {
                emptyList()
            }
        }

        override fun filterFilesToDelete(
            files: List<Path>,
        ): List<Path> {
            return emptyList()
        }
    }

}