package com.michaelflisar.lumberjack;

import android.content.Context;

/**
 * The File logging configuration.
 *
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingSetup {
    /**
     * The folder where the logs must be generated
     */
    private final String mFolderPath;
    /**
     * The log filename without the extension
     */
    private final String mFileName;
    /**
     * The log file extension
     */
    private final String mFileExtension;
    /**
     * This is the maximum file size limit for all log files generated
     */
    private final String mFileSizeLimit;
    /**
     * The number of log files to keep
     */
    private final int mLogFilesToKeep;
    /**
     * The log message pattern
     */
    private final String mLogPattern;
    /**
     * The File logging mode
     */
    private final FileLoggingMode mFileLoggingMode;
    /**
     * If the file logging(IO operations) must happen on a Non-UI thread.
     */
    private final boolean logOnBackgroundThread;

    private FileLoggingSetup(String mFolderPath, int mLogFilesToKeep, String mLogPattern, String mFileName,
                             String mFileExtension, FileLoggingMode mFileLoggingMode,
                             String mFileSizeLimit, boolean logOnBackgroundThread) {
        this.mFolderPath = mFolderPath;
        this.mLogFilesToKeep = mLogFilesToKeep;
        this.mLogPattern = mLogPattern;
        this.mFileName = mFileName;
        this.mFileExtension = mFileExtension;
        this.mFileLoggingMode = mFileLoggingMode;
        this.mFileSizeLimit = mFileSizeLimit;
        this.logOnBackgroundThread = logOnBackgroundThread;
    }

    public String getFolderPath() {
        return mFolderPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFileExtension() {
        return mFileExtension;
    }

    public int getLogFilesToKeep() {
        return mLogFilesToKeep;
    }

    public String getLogPattern() {
        return mLogPattern;
    }

    public FileLoggingMode getFileLoggingMode() {
        return mFileLoggingMode;
    }

    public String getFileSizeLimit() {
        return mFileSizeLimit;
    }

    public boolean isLogOnBackgroundThread() {
        return logOnBackgroundThread;
    }

    /**
     * Use this Builder to generate the FileLoggingSetup
     */
    public static class Builder {
        private String mFolderPath;
        private String mFileName;
        private String mFileExtension;
        private String mFileSizeLimit;
        private int mLogFilesToKeep;
        private String mLogPattern;
        private FileLoggingMode mFileLoggingMode;
        private boolean logOnBackgroundThread;

        public Builder(Context context) {
            requireNonNull(context);

            mFolderPath = context.getFileStreamPath("").getAbsolutePath();
            mLogFilesToKeep = 7;
            mLogPattern = "%d\t%msg%n";
            mFileName = "log";
            mFileExtension = "log";
            mFileLoggingMode = FileLoggingMode.DAILY_ROLLOVER;
            mFileSizeLimit = "10MB";
            logOnBackgroundThread = false;
        }

        /**
         * define a custom path for the folder, in which you want to create your log files
         * DEFAULT: the apps cache directory
         *
         * @param folderPath The path of the folder
         */
        public Builder setFolderPath(String folderPath) {
            requireNonNull(folderPath);
            this.mFolderPath = folderPath;
            return this;
        }

        /**
         * define a custom basic file name for your log files
         * DEFAULT: log
         *
         * @param fileName The basic file name of your log files
         */
        public Builder setFileName(String fileName) {
            requireNonNull(fileName);
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
            requireNonNull(fileExtension);
            this.mFileExtension = fileExtension;
            return this;
        }

        /**
         * define a custom log file pattern
         * DEFAULT: 7
         *
         * @param logFilesToKeep number of log files to keep
         */
        public Builder setLogFilesToKeep(int logFilesToKeep) {
            if (logFilesToKeep <= 0)
                throw new IllegalArgumentException("logFilesToKeep must be > 0");
            this.mLogFilesToKeep = logFilesToKeep;
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
            requireNonNull(pattern);
            this.mLogPattern = pattern;
            return this;
        }

        /**
         * define the fileLoggingMode you want to use
         *
         * Select between different {@link FileLoggingMode}
         * @param fileLoggingMode the fileLoggingMode you want
         */
        public Builder setFileLoggingMode(FileLoggingMode fileLoggingMode) {
            this.mFileLoggingMode = fileLoggingMode;
            return this;
        }

        /**
         * define the size limit for the file logger
         * you can use 1KB, 1MB and similar strings
         * DEFAULT: no file size limit
         *
         * @param fileSizeLimit the size
         */
        public Builder setFileSizeLimit(String fileSizeLimit) {
            requireNonNull(fileSizeLimit);
            this.mFileSizeLimit = fileSizeLimit;
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
            return new FileLoggingSetup(mFolderPath, mLogFilesToKeep, mLogPattern, mFileName, mFileExtension,
                    mFileLoggingMode, mFileSizeLimit, logOnBackgroundThread);
        }
    }

    static void requireNonNull(Object object) {
        if (object == null)
            throw new IllegalArgumentException("This object should not be null");
    }
}