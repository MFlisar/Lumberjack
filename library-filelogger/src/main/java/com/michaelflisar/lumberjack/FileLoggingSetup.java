package com.michaelflisar.lumberjack;

import android.content.Context;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingSetup {
    public enum Mode {
        DateFiles,
        NumberedFiles
    }

    String mFolder;

    // DEFAULT SETUP
    int mLogsToKeep = 7;
    String mLogPattern = "%d\t%msg%n";
    String mFileName = "log";
    String mFileExtension = "log";

    // Mode
    Mode mMode = Mode.DateFiles;

    // MODE: NumberedFiles
    String mNumberedFileSizeLimit = "1MB";

    boolean logOnBackgroundThread = false;

    public FileLoggingSetup(Context context) {
        // setup a default folder in the apps default path
        mFolder = context.getFileStreamPath("").getAbsolutePath();
    }

    /**
     * define a custom path for the folder, in which you want to create your log files
     * DEFAULT: the apps cache directory
     *
     * @param folderPath The path of the folder
     */
    public FileLoggingSetup withFolder(String folderPath) {
        mFolder = folderPath;
        return this;
    }

    /**
     * define a custom basic file name for your log files
     * DEFAULT: log
     *
     * @param fileName The basic file name of your log files
     */
    public FileLoggingSetup withFileName(String fileName) {
        mFileName = fileName;
        return this;
    }

    /**
     * define a custom file extension for your log files
     * DEFAULT: log
     *
     * @param fileExtension The file extension of your log files
     */
    public FileLoggingSetup withFileExtension(String fileExtension) {
        mFileExtension = fileExtension;
        return this;
    }

    /**
     * define a custom log file pattern
     * DEFAULT: 7
     *
     * @param logsToKeep number of log files to keep
     */
    public FileLoggingSetup withLogsToKeep(int logsToKeep) {
        mLogsToKeep = logsToKeep;
        return this;
    }

    /**
     * define a custom file logger format
     * Refer to https://github.com/tony19/logback-android and https://www.slf4j.org/ for more infos
     * DEFAULT: %d{HH:mm:ss.SSS}	%logger{36}	%msg%n
     *
     * @param pattern the log pattern
     */
    public FileLoggingSetup withPattern(String pattern) {
        mLogPattern = pattern;
        return this;
    }

    /**
     * define the mode you want to use
     * Select between {@link Mode#DateFiles} and {@link Mode#NumberedFiles}
     * {@link Mode#DateFiles} will create a log file per day
     * {@link Mode#NumberedFiles} will create a new_settings log file after the size defined via {@link FileLoggingSetup#withNumberedFileSizeLimit(String)} is reached
     *
     * @param mode the mode you want
     */
    public FileLoggingSetup withMode(Mode mode) {
        mMode = mode;
        return this;
    }

    /**
     * define the size limit for the file logger with mode {@link Mode#NumberedFiles}
     * you can use 1KB, 1MB and similar strings
     * DEFAULT: 1MB
     *
     * @param size the size
     */
    public FileLoggingSetup withNumberedFileSizeLimit(String size) {
        mNumberedFileSizeLimit = size;
        return this;
    }

    /**
     * define if the logging should happen on background thread to avoid IO operations on main thread
     * DEFAULT: false
     *
     * @param enabled true if background thread should be used
     */
    public FileLoggingSetup withLogOnBackgroundThread(boolean enabled) {
        logOnBackgroundThread = enabled;
        return this;
    }
}