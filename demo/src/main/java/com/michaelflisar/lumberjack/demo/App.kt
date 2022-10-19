package com.michaelflisar.lumberjack.demo

import android.app.Application
import com.michaelflisar.lumberjack.L
import com.michaelflisar.lumberjack.demo.classes.DemoLibraryWithInternalLogger

class App : Application() {

    companion object {
        // just for the demo - a global "setting" value
        var darkTheme: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        LogHelper.init(this)

        DemoLibraryWithInternalLogger.logger = { msg ->
            // +1 because we call the logging from within the App class
            // +1 because we call the logging call here
            // => this leads to logging the line where the logger itself is called
            L.callStackCorrection(2).d { msg }
        }
    }
}