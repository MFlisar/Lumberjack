package com.michaelflisar.demo

import android.app.Application
import com.michaelflisar.lumberjack.demo.DemoLogging
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.getDefaultLogFolder

class App : Application() {

    companion object {
        lateinit var setup: FileLoggerSetup
    }

    override fun onCreate() {
        super.onCreate()
        setup = FileLoggerSetup.Daily(
            folder = FileLoggerSetup.getDefaultLogFolder(this),
            fileBaseName = "log_daily"
        )
        DemoLogging.init(FileLogger(setup))
    }
}