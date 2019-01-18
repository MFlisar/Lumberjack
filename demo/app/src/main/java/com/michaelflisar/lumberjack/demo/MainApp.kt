package com.michaelflisar.lumberjack.demo

import android.app.Application

/**
 * Created by Michael on 03.11.2016.
 */

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        LogInitialiser.initLumberjack()
    }

    companion object {

        private var instance: MainApp? = null

        fun get(): MainApp? {
            return instance
        }
    }
}
