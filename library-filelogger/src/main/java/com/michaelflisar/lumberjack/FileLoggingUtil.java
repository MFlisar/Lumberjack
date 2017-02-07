package com.michaelflisar.lumberjack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by flisar on 20.10.2016.
 */

public class FileLoggingUtil
{
    public static List<String> getAllExistingLogFiles(FileLoggingSetup setup)
    {
        ArrayList<String> files = new ArrayList<>();
        File folder = new File(setup.mFolder);

//        for(File file: folder.listFiles())
//            if (!file.isDirectory())
//                file.delete();
//
//
//        return files;

        File[] listOfFiles = folder.listFiles();
        String pattern = null;
        switch (setup.mMode)
        {
            case DateFiles:
                pattern = String.format(FileLoggingTree.DATE_FILE_NAME_PATTERN, setup.mFileName, setup.mFileExtension);
                break;
            case NumberedFiles:
                pattern = String.format(FileLoggingTree.NUMBERED_FILE_NAME_PATTERN, setup.mFileName, setup.mFileExtension);
                break;
        }
        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile() && Pattern.matches(pattern, listOfFiles[i].getName()))
                files.add(listOfFiles[i].getAbsolutePath());
        }
        return files;
    }

    public static String getDefaultLogFile(FileLoggingSetup setup)
    {
        switch (setup.mMode)
        {
            case DateFiles:
                throw new RuntimeException("Can't get file, because for the date mode this file will always change!");
            case NumberedFiles:
                return setup.mFolder + "/" + setup.mFileName + "." + setup.mFileExtension;
        }
        return null;
    }
}
