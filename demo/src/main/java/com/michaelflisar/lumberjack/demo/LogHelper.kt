package com.michaelflisar.lumberjack.demo

import android.content.Context
import com.michaelflisar.lumberjack.FileLoggingSetup
import com.michaelflisar.lumberjack.FileLoggingTree
import com.michaelflisar.lumberjack.L
import timber.log.ConsoleTree

object LogHelper {

    lateinit var FILE_LOGGING_SETUP: FileLoggingSetup

    fun init(context: Context) {
        L.plant(ConsoleTree())
        // OPTIONAL: we could plant a file logger here if desired
        FILE_LOGGING_SETUP = FileLoggingSetup.DateFiles(
            context,
            setup = FileLoggingSetup.Setup(fileExtension = "txt")
        )
        L.plant(FileLoggingTree(FILE_LOGGING_SETUP))

        // we disable logs in release inside this demo
        L.enabled = BuildConfig.DEBUG

        // we can filter out logs based on caller class names like following e.g.:
//        L.packageNameFilter = {
//            Log.d("PACKAGE_NAME_FILTER", "packageName = $it")
//             !it.startsWith(MainActivity::class.java.name)
//        }
    }

    fun clearLogFiles() {
        // do NOT delete all files directly, just delete old ones and clear the newest one => the following function does do that for you
        FILE_LOGGING_SETUP.clearLogFiles()
    }
}