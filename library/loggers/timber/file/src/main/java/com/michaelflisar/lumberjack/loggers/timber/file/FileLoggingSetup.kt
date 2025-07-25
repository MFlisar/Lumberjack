package com.michaelflisar.lumberjack.loggers.timber.file

import android.content.Context
import android.os.Parcelable
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path
import java.io.File
import java.io.PrintWriter
import java.util.regex.Pattern

sealed class FileLoggingSetup : IFileLoggingSetup {

    protected abstract val pattern: String
    abstract val folder: String
    abstract val logOnBackgroundThread: Boolean
    abstract val setup: Setup

    override fun getAllExistingLogFilePaths() =
        getFilePathsInFolder().filter { Pattern.matches(pattern, it.name) }

    override fun getLatestLogFilePath() =
        getAllExistingLogFilePaths().maxByOrNull { File(it.toString()).lastModified() }

    override suspend fun clearLogFiles() {
        withContext(Dispatchers.IO) {
            val newestFile = getLatestLogFilePath()
            val filesToDelete = getAllExistingLogFilePaths().filter { it != newestFile }
            filesToDelete.forEach {
                File(it.toString()).delete()
            }
            newestFile?.let {
                val writer = PrintWriter(File(it.toString()))
                writer.print("")
                writer.close()
            }
        }
    }

    @Parcelize
    class NumberedFiles(
        override val folder: String,
        override val logOnBackgroundThread: Boolean,
        val sizeLimit: String,
        override val setup: Setup,
        override val fileConverter: IFileConverter = FileConverter
    ) : FileLoggingSetup() {

        constructor(
            context: Context,
            folder: String = context.getFileStreamPath("").absolutePath,
            logOnBackgroundThread: Boolean = true,
            sizeLimit: String = "1MB",
            setup: Setup = Setup()
        ) : this(folder, logOnBackgroundThread, sizeLimit, setup)

        @IgnoredOnParcel
        override val pattern = String.format(
            FileLoggingTree.NUMBERED_FILE_NAME_PATTERN,
            setup.fileName,
            setup.fileExtension
        )

        @IgnoredOnParcel
        val baseFilePath = folder + "/" + setup.fileName + "." + setup.fileExtension

        /*
         * we know the file name of the newest file before hand, no need to check all files in the folder
         */
        override fun getLatestLogFilePath() = Path(baseFilePath)
    }

    @Parcelize
    class DateFiles(
        override val folder: String,
        override val logOnBackgroundThread: Boolean,
        override val setup: Setup,
        override val fileConverter: IFileConverter = FileConverter
    ) : FileLoggingSetup() {

        constructor(
            context: Context,
            folder: String = context.getFileStreamPath("").absolutePath,
            logOnBackgroundThread: Boolean = true,
            setup: Setup = Setup()
        ) : this(folder, logOnBackgroundThread, setup)

        @IgnoredOnParcel
        override val pattern = String.format(
            FileLoggingTree.DATE_FILE_NAME_PATTERN,
            setup.fileName,
            setup.fileExtension
        )
    }

    /*
     * logsToKeep...    define how many log files will be kept
     * logPattern...    define a custom file logger format
     *                  Refer to https://github.com/tony19/logback-android and https://www.slf4j.org/ for more infos
     *                  DEFAULT: %d{HH:mm:ss.SSS}	%logger{36}	%msg%n
     * fileName...      define a custom basic file name for your log files
     * fileExtension... define a custom file extension for your log files
     */
    @Parcelize
    class Setup(
        val logsToKeep: Int = 7,
        val logPattern: String = "%d %marker%-5level %msg%n",
        val fileName: String = "log",
        val fileExtension: String = "log"
    ) : Parcelable

    private fun getFilePathsInFolder(): List<Path> {
        val folder = File(folder)
        if (!folder.exists()) {
            return emptyList()
        }
        return folder.listFiles()?.filter { it.isFile }?.map { Path(it.absolutePath) } ?: emptyList()
    }
}