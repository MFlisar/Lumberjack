package com.michaelflisar.lumberjack.loggers.console

import android.util.Log
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.DefaultLumberjackFilter
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger

class ConsoleLogger(
    override var enabled: Boolean = true,
    override val filter: LumberjackFilter = DefaultLumberjackFilter
) : ILumberjackLogger {

    override fun log(
        level: Level,
        tag: String?,
        time: Long,
        fileName: String,
        className: String,
        methodName: String,
        line: Int,
        msg: String?,
        throwable: Throwable?
    ) {
        val link = "(${fileName}:${line})"
        val log = listOfNotNull(
            msg,
            link.takeIf { throwable == null },
            throwable?.stackTraceToString()?.let { "\n$it" }
        ).joinToString(" ")
        Log.println(level.priority, tag, log)
    }

}