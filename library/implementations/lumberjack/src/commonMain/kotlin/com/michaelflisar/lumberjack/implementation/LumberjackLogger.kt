package com.michaelflisar.lumberjack.implementation

import co.touchlab.stately.concurrency.ThreadLocalRef
import com.michaelflisar.lumberjack.core.AbstractLogger
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.StackData
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import kotlinx.datetime.Clock

object LumberjackLogger : AbstractLogger() {

    private val implementations: MutableList<ILumberjackLogger> = ArrayList()
    private val explicitTag = ThreadLocalRef<String>()
    private val callStackCorrection = ThreadLocalRef<Int>()

    // -------------------
    // Object functions
    // -------------------

    internal fun register(implementation: ILumberjackLogger) {
        implementations += implementation
    }

    internal fun unregister(implementation: ILumberjackLogger) {
        implementations -= implementation
    }

    internal fun unregisterAll() {
        implementations.clear()
    }

    fun loggers(): List<ILumberjackLogger> = implementations

    // -------------------
    // API
    // -------------------

    override fun callStackCorrection(value: Int): LumberjackLogger {
        callStackCorrection.set(value)
        return LumberjackLogger
    }

    override fun tag(tag: String): LumberjackLogger {
        explicitTag.set(tag)
        return LumberjackLogger
    }

    // -------------------
    // Internal/Helper functions
    // -------------------

    override fun doLog(
        level: Level,
        message: String?,
        t: Throwable?,
        t2: Throwable
    ) {
        val tag = getTag()
        val correction = getCallStackCorrection() ?: 0
        val stackTrace = StackData(t ?: t2, correction + if (t == null) 1 else 0)
        val fileName = stackTrace.fileName
        val className = stackTrace.className
        val methodName = stackTrace.methodName
        val line = stackTrace.line
        val time = Clock.System.now().toEpochMilliseconds()
        implementations.forEach {
            if (it.isEnabled(level) && it.filter(
                    level,
                    tag,
                    time,
                    fileName,
                    className,
                    methodName,
                    line,
                    message,
                    t
                )
            ) {
                it.log(level, tag, time, fileName, className, methodName, line, message, t)
            }
        }
    }

    private fun getTag(): String? {
        val tag = explicitTag.get()
        if (tag != null) {
            explicitTag.remove()
        }
        return tag
    }

    private fun getCallStackCorrection(): Int? {
        val correction = callStackCorrection.get()
        if (correction != null) {
            callStackCorrection.remove()
        }
        return correction
    }
}