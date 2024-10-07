package com.michaelflisar.lumberjack.demo

import com.michaelflisar.demoutilities.DemoApp

class App : DemoApp() {

    override fun onCreate() {
        super.onCreate()
        DemoLogging.init(this)
    }
}