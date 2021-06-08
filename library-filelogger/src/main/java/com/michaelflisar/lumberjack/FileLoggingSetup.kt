package com.michaelflisar.lumberjack

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.util.regex.Pattern

sealed class FileLoggingSetup {

    protected abstract val pattern: String
    abstract val context: Context
    abstract val folder: String
    abstract val logOnBackgroundThread: Boolean
    abstract val setup: Setup

    fun getAllExistingLogFiles() = getFilesInFolder().filter { Pattern.matches(pattern, it.name) }
    open fun getLatestLogFiles() = getAllExistingLogFiles().sortedByDescending { it.lastModified() }.firstOrNull()

    fun clearLogFiles() {
        val newestFile = getLatestLogFiles()
        val filesToDelete = getAllExistingLogFiles().filter { it != newestFile }
        filesToDelete.forEach {
            it.delete()
        }
        newestFile?.let {
            val writer = PrintWriter(it)
            writer.print("")
            writer.close()
        }
    }

    class NumberedFiles(
        override val context: Context,
        override val folder: String = context.getFileStreamPath("").absolutePath,
        override val logOnBackgroundThread: Boolean = true,
        val sizeLimit: String = "1MB",
        override val setup: Setup = Setup()
    ) : FileLoggingSetup() {
        override val pattern = String.format(
            FileLoggingTree.NUMBERED_FILE_NAME_PATTERN,
            setup.fileName,
            setup.fileExtension
        )
        val baseFilePath = folder + "/" + setup.fileName + "." + setup.fileExtension
        /*
         * we know the file name of the newest file before hand, no need to check all files in the folder
         */
        override fun getLatestLogFiles() = File(baseFilePath)
    }

    class DateFiles(
        override val context: Context,
        override val folder: String = context.getFileStreamPath("").absolutePath,
        override val logOnBackgroundThread: Boolean = true,
        override val setup: Setup = Setup()
    ) : FileLoggingSetup() {
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
    class Setup(
        val logsToKeep: Int = 7,
        val logPattern: String = "%d %-5level %msg%n",
        val fileName: String = "log",
        val fileExtension: String = "log"
    )

    protected fun getFilesInFolder(): List<File> {
        val folder = File(folder)
        if (!folder.exists()) {
            return emptyList()
        }
        return folder.listFiles()?.filter { it.isFile } ?: emptyList()
    }
}