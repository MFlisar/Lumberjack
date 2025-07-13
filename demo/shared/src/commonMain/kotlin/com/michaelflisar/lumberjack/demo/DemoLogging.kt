package com.michaelflisar.lumberjack.demo

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object DemoLogging {

    fun init(
        fileLogger: ILumberjackLogger?
    ) {
        // 1) set the implementation that is used
        L.init(LumberjackLogger)

        // 2) plant "trees"
        L.plant(ConsoleLogger())
        if (fileLogger != null) {
            L.plant(fileLogger)
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

    fun run(scope: CoroutineScope, ioContext: CoroutineContext) {

        L.d { "TEST-LOG - 1a - MainActivity onCreate was just called" }
        L.e(Exception("TEST")) { "TEST-LOG - 1b - MainActivity onCreate was just called" }
        L.logIf { true }?.d { "TEST-LOG - 1c - MainActivity onCreate was just called" }

        L.logIf { false }?.d {
            // this block will never be executed thanks to lazy evaluation
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

        scope.launch(ioContext) {
            L.d { "TEST-LOG - 6 - from within coroutine on background thread (if the platform has one)..." }
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

    fun runTest() {
        // This is just a test to ensure that the code compiles and runs
        val test = Test()
        test.testLogInClass()
    }

    private class TestException(msg: String) : Throwable(msg) {
        override fun toString(): String {
            return message!!
        }
    }
}


class Test {

    fun testLogInClass() {
        L.d { "Test log in class" }
    }
}