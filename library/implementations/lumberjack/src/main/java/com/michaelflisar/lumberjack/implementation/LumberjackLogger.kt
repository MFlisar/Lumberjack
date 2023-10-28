package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.AbstractLogger
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.StackData
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger
import java.util.Calendar

object LumberjackLogger : AbstractLogger() {

    private var enabled: Boolean = true
    private val implementations: MutableList<ILumberjackLogger> = ArrayList()
    private val explicitTag = ThreadLocal<String>()
    private val callStackCorrection = ThreadLocal<Int>()

    // -------------------
    // Object functions
    // -------------------

    internal fun register(implementation: ILumberjackLogger) {
        implementations += implementation
    }

    fun enable(enabled: Boolean) {
        LumberjackLogger.enabled = enabled
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
        val time = Calendar.getInstance().timeInMillis
        implementations.forEach {
            if (it.enabled && it.filter(
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

    override fun isEnabled(): Boolean {
        return enabled && implementations.find { it.enabled } != null
    }
}