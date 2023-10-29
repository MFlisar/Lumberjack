package com.michaelflisar.lumberjack.demo

import android.content.Context
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.lumberjack.demo.classes.DemoLibraryWithInternalLogger
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.implementation.timber.TimberLogger
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.lumberjack.loggers.timber.file.FileLoggingSetup
import com.michaelflisar.lumberjack.loggers.timber.file.FileLoggingTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.ConsoleTree
import timber.log.Timber

object DemoLogging {

    lateinit var FILE_LOGGING_SETUP: IFileLoggingSetup

    fun init(context: Context) {

        // settings for this demo app
        val useTimber = false // use timber or use lumberjacks own logging manager (preferred!)

        // 1) set the implementation that is used
        L.init(if (useTimber) TimberLogger else LumberjackLogger)

        // 2) plant "trees"
        if (useTimber) {
            val setup = FileLoggingSetup.DateFiles(context).also {
                FILE_LOGGING_SETUP = it
            }
            Timber.plant(ConsoleTree())
            Timber.plant(FileLoggingTree(setup))
        } else {
            val setup = FileLoggerSetup.Daily(context).also {
                FILE_LOGGING_SETUP = it
            }
            L.plant(ConsoleLogger())
            L.plant(FileLogger(setup))
        }

        // EXAMPLE on how you could use lumberjack inside a library with the minimal dependency on the core module
        // or how to log messages from other libraries that provide to plug in a logger...
        DemoLibraryWithInternalLogger.logger = { msg ->
            // +1 because we call the logging from within the App class
            // +1 because we call the logging call here
            // => this leads to logging the line where the logger itself is called
            L.callStackCorrection(3).d { msg }
        }
    }

    fun run(scope: CoroutineScope) {

        L.d { "TEST-LOG - 1a - MainActivity onCreate was just called" }
        L.e(Exception("TEST")) { "TEST-LOG - 1b - MainActivity onCreate was just called" }
        L.logIf { true }?.d { "TEST-LOG - 1c - MainActivity onCreate was just called" }

        L.logIf { false }?.d {
            // sleep 60s - no problem, this block will never be executed thanks to lazy evaluation
            Thread.sleep(1000 * 60)
            "TEST-LOG - 2 - this log will never be printed nor will this block ever be executed"
        }
        L.e { "TEST-LOG - 3 - Some error message" }
        L.e(TestException("TEST-LOG - 4 - TestException (some info)"))

        // --------------
        // Specials
        // --------------

        val func = { info: String ->
            L.d { "TEST-LOG - 5 - from within lambda: $info" }
        }

        func("func call 1...")
        func("func call 2...")
        func("func call 3...")

        scope.launch(Dispatchers.IO) {
            L.d { "TEST-LOG - 6 - from within coroutine on background thread: ${Thread.currentThread()}" }
        }

        for (i in 0..10) {
            L.d { "TEST-LOG - Test $i" }
        }

        scope.launch {
            L.tag("LEVEL").v { "TEST-LOG - Verbose log..." }
            L.tag("LEVEL").d { "TEST-LOG - Debug log..." }
            L.tag("LEVEL").i { "TEST-LOG - Info log..." }
            L.tag("LEVEL").w { "TEST-LOG - Warn log..." }
            L.tag("LEVEL").e { "TEST-LOG - Error log..." }
            L.tag("LEVEL").wtf { "TEST-LOG - WTF log..." }
        }

        // call stack correction inside forwarded logger
        // this should log THIS line inside the custom logger
        DemoLibraryWithInternalLogger.run()

        // Logging with level
        L.tag("MANUAL LEVEL").log(Level.DEBUG) { "TEST-LOG - Debug log..." }
        L.tag("MANUAL LEVEL").log(Level.ERROR, Exception("EX")) { "TEST-LOG - Error log..." }
    }

    private class TestException(msg: String) : Throwable(msg) {
        override fun toString(): String {
            return message!!
        }
    }
}