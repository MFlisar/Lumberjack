package com.michaelflisar.lumberjack.demo

class App : com.michaelflisar.toolbox.androiddemoapp.DemoApp() {

    override fun onCreate() {
        super.onCreate()
        DemoLogging.init(this)
    }
}