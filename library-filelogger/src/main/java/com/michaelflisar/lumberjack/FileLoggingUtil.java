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
        File[] listOfFiles = folder.listFiles();
        String pattern = String.format(FileLoggingTree.FILE_NAME_PATTERN, setup.mFileName, setup.mFileExtension);
        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile() && Pattern.matches(pattern, listOfFiles[i].getName()))
                files.add(listOfFiles[i].getAbsolutePath());
        }
        return files;
    }
}
