package com.michaelflisar.lumberjack.demo

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DemoLogging.init(this)
    }
}