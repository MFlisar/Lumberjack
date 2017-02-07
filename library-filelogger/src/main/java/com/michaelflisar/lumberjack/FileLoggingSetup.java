package com.michaelflisar.lumberjack;

import android.content.Context;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingSetup
{
    public enum Mode
    {
        DateFiles,
        NumberedFiles
    }

    String mFolder;

    // DEFAULT SETUP
    int mLogsToKeep = 7;
    String mLogPattern = "%d{HH:mm:ss.SSS}\t%logger{36}\t%msg%n";
    String mFileName = "log";
    String mFileExtension= "log";

    // Mode
    Mode mMode = Mode.DateFiles;

    // MODE: NumberedFiles
    String mNumberedFileSizeLimit = "1MB";

    public FileLoggingSetup(Context context)
    {
        // setup a default folder in the apps default path
        mFolder = context.getFileStreamPath("").getAbsolutePath();
    }

    public FileLoggingSetup withFolder(String folderPath)
    {
        mFolder = folderPath;
        return this;
    }

    public FileLoggingSetup withFileName(String fileName)
    {
        mFileName = fileName;
        return this;
    }

    public FileLoggingSetup withFileExtension(String fileExtension)
    {
        mFileExtension = fileExtension;
        return this;
    }

    public FileLoggingSetup withLogsToKeep(int logsToKeep)
    {
        mLogsToKeep = logsToKeep;
        return this;
    }

    public FileLoggingSetup withPattern(String pattern)
    {
        mLogPattern = pattern;
        return this;
    }

    public FileLoggingSetup withMode(Mode mode)
    {
        mMode = mode;
        return this;
    }

    public FileLoggingSetup withNumberedFileSizeLimit(String size)
    {
        mNumberedFileSizeLimit = size;
        return this;
    }
}