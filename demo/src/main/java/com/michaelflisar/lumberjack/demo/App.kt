package com.michaelflisar.lumberjack.demo

import android.app.Application

class App : Application() {

    companion object {
        // just for the demo - a global "setting" value
        var darkTheme: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        LogHelper.init(this)
    }
}