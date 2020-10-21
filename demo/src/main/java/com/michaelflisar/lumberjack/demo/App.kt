package com.michaelflisar.lumberjack.demo

import android.app.Application
import android.util.Log
import com.michaelflisar.lumberjack.L
import timber.log.ConsoleTree

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        L.plant(ConsoleTree())
        // we could plant a file logger here if desired
        // L.plant(FileLoggingTree(FileLoggingSetup(this)))

        // we disable logs in release inside this demo
        L.enabled = BuildConfig.DEBUG

        // we can filter out logs based on caller class names like following e.g.:
//        L.packageNameFilter = {
//            Log.d("PACKAGE_NAME_FILTER", "packageName = $it")
//             !it.startsWith(MainActivity::class.java.name)
//        }
    }
}