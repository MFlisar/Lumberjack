package com.michaelflisar.lumberjack

import java.io.File
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * Created by flisar on 20.10.2016.
 */

object FileLoggingUtil {
    fun getAllExistingLogFiles(setup: FileLoggingSetup): List<String> {
        val files = ArrayList<String>()
        val folder = File(setup.folder)

        val listOfFiles = folder.listFiles()
        var pattern: String? = null
        when (setup.mode) {
            FileLoggingSetup.Mode.DateFiles -> pattern = String.format(FileLoggingTree.DATE_FILE_NAME_PATTERN, setup.fileName, setup.fileExtension)
            FileLoggingSetup.Mode.NumberedFiles -> pattern = String.format(FileLoggingTree.NUMBERED_FILE_NAME_PATTERN, setup.fileName, setup.fileExtension)
        }
        for (i in listOfFiles.indices) {
            if (listOfFiles[i].isFile && Pattern.matches(pattern, listOfFiles[i].name))
                files.add(listOfFiles[i].absolutePath)
        }
        return files
    }

    fun getDefaultLogFile(setup: FileLoggingSetup): String? {
        when (setup.mode) {
            FileLoggingSetup.Mode.DateFiles -> throw RuntimeException("Can't get file, because for the date mode this file will always change!")
            FileLoggingSetup.Mode.NumberedFiles -> return setup.folder + "/" + setup.fileName + "." + setup.fileExtension
        }
        return null
    }
}
