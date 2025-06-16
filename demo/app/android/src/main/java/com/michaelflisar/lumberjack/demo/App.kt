package com.michaelflisar.lumberjack.demo

import android.app.Application
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.file.create

class App : Application() {

    companion object {
        lateinit var setup: FileLoggerSetup
    }

    override fun onCreate() {
        super.onCreate()
        setup = FileLoggerSetup.Daily.create(this, fileBaseName = "log_daily")
        DemoLogging.init(FileLogger(setup))
    }
}