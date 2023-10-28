package com.michaelflisar.lumberjack.demo

import android.app.Application
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.demo.classes.DemoLibraryWithInternalLogger
import com.michaelflisar.lumberjack.implementation.timber.TimberLogger
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.timber.file.FileLoggingSetup
import com.michaelflisar.lumberjack.loggers.timber.file.FileLoggingTree
import timber.log.ConsoleTree
import timber.log.Timber

class App : Application() {

    companion object {
        // just for the demo - a global "setting" value
        var darkTheme: Boolean = false

        lateinit var FILE_LOGGING_SETUP: IFileLoggingSetup
    }

    override fun onCreate() {
        super.onCreate()
        initLogging()
    }

    private fun initLogging() {

        // settings for this demo app
        val useTimber = false // use timber or use lumberjacks own logging manager (preferred!)

        // 1) set the implementation that is used
        L.init(if (useTimber) TimberLogger else LumberjackLogger)

        // 2) plant "trees"
        if (useTimber) {
            val setup = FileLoggingSetup.DateFiles(this  ).also {
                FILE_LOGGING_SETUP = it
            }
            Timber.plant(ConsoleTree())
            Timber.plant(FileLoggingTree(setup))
        } else {
            val setup = FileLoggerSetup.Daily(this).also {
                FILE_LOGGING_SETUP = it
            }
            L.plant( ConsoleLogger())
            L.plant(FileLogger(setup))
        }

        // EXAMPLE on how you could use lumberjack inside a library with the minimal dependency on the core module
        // or how to log messages from other libraries that provide to plug in a logger...
        DemoLibraryWithInternalLogger.logger = { msg ->
            // +1 because we call the logging from within the App class
            // +1 because we call the logging call here
            // => this leads to logging the line where the logger itself is called
            L.callStackCorrection(3).d { msg }
        }
    }
}