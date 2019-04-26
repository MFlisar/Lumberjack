package com.michaelflisar.lumberjack;

import android.content.Context;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingSetup {
    private final String mFolder;
    private final int mLogsToKeep;
    private final String mLogPattern;
    private final String mFileName;
    private final String mFileExtension;
    private final FileLoggingMode mFileLoggingMode;
    private final String mNumberedFileSizeLimit;
    private final boolean logOnBackgroundThread;

    private FileLoggingSetup(String mFolder, int mLogsToKeep, String mLogPattern, String mFileName, String mFileExtension, FileLoggingMode mFileLoggingMode, String mNumberedFileSizeLimit, boolean logOnBackgroundThread) {
        this.mFolder = mFolder;
        this.mLogsToKeep = mLogsToKeep;
        this.mLogPattern = mLogPattern;
        this.mFileName = mFileName;
        this.mFileExtension = mFileExtension;
        this.mFileLoggingMode = mFileLoggingMode;
        this.mNumberedFileSizeLimit = mNumberedFileSizeLimit;
        this.logOnBackgroundThread = logOnBackgroundThread;
    }

    public String getFolder() {
        return mFolder;
    }

    public int getLogsToKeep() {
        return mLogsToKeep;
    }

    public String getLogPattern() {
        return mLogPattern;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFileExtension() {
        return mFileExtension;
    }

    public FileLoggingMode getFileLoggingMode() {
        return mFileLoggingMode;
    }

    public String getNumberedFileSizeLimit() {
        return mNumberedFileSizeLimit;
    }

    public boolean isLogOnBackgroundThread() {
        return logOnBackgroundThread;
    }


    public static class Builder {
        private String mFolder;
        private int mLogsToKeep;
        private String mLogPattern;
        private String mFileName;
        private String mFileExtension;
        private FileLoggingMode mFileLoggingMode;
        private String mNumberedFileSizeLimit;
        private boolean logOnBackgroundThread;

        public Builder(Context context) {
            mFolder = context.getFileStreamPath("").getAbsolutePath();
            mLogsToKeep = 7;
            mLogPattern = "%d\t%msg%n";
            mFileName = "log";
            mFileExtension = "log";
            mFileLoggingMode = FileLoggingMode.DATE_FILES;
            mNumberedFileSizeLimit = "1MB";
            logOnBackgroundThread = false;
        }

        /**
         * define a custom path for the folder, in which you want to create your log files
         * DEFAULT: the apps cache directory
         *
         * @param folderPath The path of the folder
         */
        public Builder setFolder(String folderPath) {
            this.mFolder = folderPath;
            return this;
        }

        /**
         * define a custom basic file name for your log files
         * DEFAULT: log
         *
         * @param fileName The basic file name of your log files
         */
        public Builder setFileName(String fileName) {
            this.mFileName = fileName;
            return this;
        }

        /**
         * define a custom file extension for your log files
         * DEFAULT: log
         *
         * @param fileExtension The file extension of your log files
         */
        public Builder setFileExtension(String fileExtension) {
            this.mFileExtension = fileExtension;
            return this;
        }

        /**
         * define a custom log file pattern
         * DEFAULT: 7
         *
         * @param logsToKeep number of log files to keep
         */
        public Builder setLogsToKeep(int logsToKeep) {
            this.mLogsToKeep = logsToKeep;
            return this;
        }

        /**
         * define a custom file logger format
         * Refer to https://github.com/tony19/logback-android and https://www.slf4j.org/ for more infos
         * DEFAULT: %d{HH:mm:ss.SSS}	%logger{36}	%msg%n
         *
         * @param pattern the log pattern
         */
        public Builder setLogPattern(String pattern) {
            this.mLogPattern = pattern;
            return this;
        }

        /**
         * define the fileLoggingMode you want to use
         *
         * Select between {@link FileLoggingMode#DATE_FILES} and {@link FileLoggingMode#NUMBERED_FILES}
         *
         * - {@link FileLoggingMode#DATE_FILES} will create a log file per day
         * - {@link FileLoggingMode#NUMBERED_FILES} will create a new log file after the fileSizeLimit
         * defined via {@link FileLoggingSetup#mNumberedFileSizeLimit} is reached.
         *
         * @param fileLoggingMode the fileLoggingMode you want
         */
        public Builder setFileLoggingMode(FileLoggingMode fileLoggingMode) {
            this.mFileLoggingMode = fileLoggingMode;
            return this;
        }

        /**
         * define the size limit for the file logger with mode {@link FileLoggingMode#NUMBERED_FILES}
         * you can use 1KB, 1MB and similar strings
         * DEFAULT: 1MB
         *
         * @param fileSizeLimit the size
         */
        public Builder setNumberedFileSizeLimit(String fileSizeLimit) {
            this.mNumberedFileSizeLimit = fileSizeLimit;
            return this;
        }

        /**
         * define if the logging should happen on background thread to avoid IO operations on main thread
         * DEFAULT: false
         *
         * @param enabled true if background thread should be used
         */
        public Builder setLogOnBackgroundThread(boolean enabled) {
            this.logOnBackgroundThread = enabled;
            return this;
        }

        public FileLoggingSetup build() {
            return new FileLoggingSetup(mFolder, mLogsToKeep, mLogPattern, mFileName, mFileExtension,
                    mFileLoggingMode, mNumberedFileSizeLimit, logOnBackgroundThread);
        }
    }
}