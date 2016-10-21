package com.michaelflisar.lumberjack;

import android.content.Context;

/**
 * Created by Michael on 17.10.2016.
 */

public class FileLoggingSetup
{
    String mFolder;

    // DEFAULT SETUP
    int mDaysToKeep = 7;
    String mLogPattern = "%d{HH:mm:ss.SSS}\t%logger{36}\t%msg%n";
    String mFileName = "log";
    String mFileExtension= "log";

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

    public FileLoggingSetup withDaysToKeep(int daysToKeep)
    {
        mDaysToKeep = daysToKeep;
        return this;
    }

    public FileLoggingSetup withPattern(String pattern)
    {
        mLogPattern = pattern;
        return this;
    }
}