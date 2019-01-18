package com.michaelflisar.lumberjack.demo

import com.michaelflisar.lumberjack.FileLoggingSetup
import com.michaelflisar.lumberjack.FileLoggingTree
import com.michaelflisar.lumberjack.L
import timber.log.ConsoleTree

/**
 * Created by Michael on 03.11.2016.
 */

object LogInitialiser {

    // -----------------------------
    // Setup
    // -----------------------------

    /*
     * this initialised the Logger - we simply plant two trees in this demo
     */
    fun initLumberjack() {
        // simply console logger
        L.plant(ConsoleTree())
        // a file logger
        L.plant(FileLoggingTree(FILE_LOG_SETUP))
    }

    // -----------------------------
    // File Logger
    // -----------------------------

    val FILE_LOG_SETUP by lazy {
        FileLoggingSetup(MainApp.get()!!)
    }
}