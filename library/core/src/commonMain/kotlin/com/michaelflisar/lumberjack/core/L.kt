package com.michaelflisar.lumberjack.core

import com.michaelflisar.lumberjack.core.classes.Level

object L : AbstractLogger() {

    private var implementation: AbstractLogger? = null

    fun init(implementation: AbstractLogger) {
        this.implementation = implementation
    }

    // ----------------------------------
    // forward everything to the implementation
    // ----------------------------------

    override fun callStackCorrection(value: Int): AbstractLogger {
        implementation!!.callStackCorrection(value)
        return this
    }

    override fun tag(tag: String): AbstractLogger {
        implementation!!.tag(tag)
        return this
    }

    override fun doLog(level: Level, message: String?, t: Throwable?, t2: Throwable) {
        implementation!!.doLog(level, message, t, t2)
    }

    override fun isEnabled(level: Level): Boolean {
       return implementation!!.isEnabled(level)
    }

    override fun enable(minLogLevel: Level) {
        implementation!!.enable(minLogLevel)
    }
}