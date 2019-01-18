package com.michaelflisar.lumberjack

import android.content.Context

/**
 * Created by Michael on 17.10.2016.
 */

class FileLoggingSetup(context: Context) {

    internal var folder: String

    // DEFAULT SETUP
    internal var logsToKeep = 7
    internal var logPattern = "%d\t%msg%n"
    internal var fileName = "log"
    internal var fileExtension = "log"

    // Mode
    internal var mode = Mode.DateFiles

    // MODE: NumberedFiles
    internal var numberedFileSizeLimit = "1MB"

    internal var logOnBackgroundThread = false

    enum class Mode {
        DateFiles,
        NumberedFiles
    }

    init {
        // setup a default folder in the apps default path
        folder = context.getFileStreamPath("").absolutePath
    }

    /**
     * define a custom path for the folder, in which you want to create your log files
     * DEFAULT: the apps cache directory
     *
     * @param folderPath The path of the folder
     */
    fun withFolder(folderPath: String): FileLoggingSetup {
        folder = folderPath
        return this
    }

    /**
     * define a custom basic file name for your log files
     * DEFAULT: log
     *
     * @param fileName The basic file name of your log files
     */
    fun withFileName(fileName: String): FileLoggingSetup {
        this.fileName = fileName
        return this
    }

    /**
     * define a custom file extension for your log files
     * DEFAULT: log
     *
     * @param fileExtension The file extension of your log files
     */
    fun withFileExtension(fileExtension: String): FileLoggingSetup {
        this.fileExtension = fileExtension
        return this
    }

    /**
     * define a custom log file pattern
     * DEFAULT: 7
     *
     * @param logsToKeep number of log files to keep
     */
    fun withLogsToKeep(logsToKeep: Int): FileLoggingSetup {
        this.logsToKeep = logsToKeep
        return this
    }

    /**
     * define a custom file logger format
     * Refer to https://github.com/tony19/logback-android and https://www.slf4j.org/ for more infos
     * DEFAULT: %d{HH:mm:ss.SSS}	%logger{36}	%msg%n
     *
     * @param pattern the log pattern
     */
    fun withPattern(pattern: String): FileLoggingSetup {
        logPattern = pattern
        return this
    }

    /**
     * define the mode you want to use
     * Select between [Mode.DateFiles] and [Mode.NumberedFiles]
     * [Mode.DateFiles] will create a log file per day
     * [Mode.NumberedFiles] will create a new_settings log file after the size defined via [FileLoggingSetup.withNumberedFileSizeLimit] is reached
     *
     * @param mode the mode you want
     */
    fun withMode(mode: Mode): FileLoggingSetup {
        this.mode = mode
        return this
    }

    /**
     * define the size limit for the file logger with mode [Mode.NumberedFiles]
     * you can use 1KB, 1MB and similar strings
     * DEFAULT: 1MB
     *
     * @param size the size
     */
    fun withNumberedFileSizeLimit(size: String): FileLoggingSetup {
        numberedFileSizeLimit = size
        return this
    }

    /**
     * define if the logging should happen on background thread to avoid IO operations on main thread
     * DEFAULT: false
     *
     * @param enabled true if background thread should be used
     */
    fun withLogOnBackgroundThread(enabled: Boolean): FileLoggingSetup {
        logOnBackgroundThread = enabled
        return this
    }
}