package com.michaelflisar.lumberjack.core

import com.michaelflisar.lumberjack.core.classes.Level

abstract class AbstractLogger {

    // -------------------
    // API
    // -------------------

    fun logIf(block: () -> Boolean): AbstractLogger? {
        if (block()) {
            return this
        } else {
            return null
        }
    }

    abstract fun callStackCorrection(value: Int): AbstractLogger

    abstract fun tag(tag: String): AbstractLogger

    fun v(t: Throwable, message: () -> String) = log(Level.VERBOSE, t, t, message, ::doLog)
    fun v(t: Throwable) = log(Level.VERBOSE, t, t, null, ::doLog)
    fun v(message: () -> String) = log(Level.VERBOSE, null, Throwable(), message, ::doLog)
    fun d(t: Throwable, message: () -> String) = log(Level.DEBUG, t, t, message, ::doLog)
    fun d(t: Throwable) = log(Level.DEBUG, t, t, null, ::doLog)
    fun d(message: () -> String) = log(Level.DEBUG, null, Throwable(), message, ::doLog)
    fun i(t: Throwable, message: () -> String) = log(Level.INFO, t, t, message, ::doLog)
    fun i(t: Throwable) = log(Level.INFO, t, t, null, ::doLog)
    fun i(message: () -> String) = log(Level.INFO, null, Throwable(), message, ::doLog)
    fun w(t: Throwable, message: () -> String) = log(Level.WARN, t, t, message, ::doLog)
    fun w(t: Throwable) = log(Level.WARN, t, t, null, ::doLog)
    fun w(message: () -> String) = log(Level.WARN, null, Throwable(), message, ::doLog)
    fun e(t: Throwable, message: () -> String) = log(Level.ERROR, t, t, message, ::doLog)
    fun e(t: Throwable) = log(Level.ERROR, t, t, null, ::doLog)
    fun e(message: () -> String) = log(Level.ERROR, null, Throwable(), message, ::doLog)
    fun wtf(t: Throwable, message: () -> String) = log(Level.WTF, t, t, message, ::doLog)
    fun wtf(t: Throwable) = log(Level.WTF, t, t, null, ::doLog)
    fun wtf(message: () -> String) = log(Level.WTF, null, Throwable(), message, ::doLog)
    fun log(level: Level, t: Throwable, message: () -> String) = log(level, t, t, message, ::doLog)
    fun log(level: Level, t: Throwable) = log(level, t, t, null, ::doLog)
    fun log(level: Level, message: () -> String) = log(level, null, Throwable(), message, ::doLog)

    @PublishedApi
    internal inline fun log(
        level: Level,
        t: Throwable?,
        t2: Throwable,
        noinline message: (() -> String)?,
        log: (level: Level, message: String?, t: Throwable?, t2: Throwable) -> Unit
    ) {
        if (isEnabled()) {
            log(level, message?.invoke(), t, t2)
        }
        return
    }

    // -------------------
    // Helper functions
    // -------------------

    abstract fun doLog(
        level: Level,
        message: String?,
        t: Throwable?,
        t2: Throwable
    )

    abstract fun isEnabled(): Boolean
    abstract fun enable(enabled: Boolean)
}

