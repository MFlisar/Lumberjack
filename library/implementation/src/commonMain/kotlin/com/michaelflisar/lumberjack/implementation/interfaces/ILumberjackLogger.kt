package com.michaelflisar.lumberjack.implementation.interfaces

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter

interface ILumberjackLogger {


    /**
     * the minimum log level that is logged by this logger
     */
    val minLogLevel: Level

    /*
    * if false, logging of this level is disabled
    */
    fun isEnabled(level: Level): Boolean {
        return minLogLevel != Level.NONE && minLogLevel.order <= level.order
    }

    /*
    * provide a filter to filter out logs based on content, tags, class names, ...
    */
    val filter: LumberjackFilter

    fun log(level: Level, tag: String?, time: Long, fileName: String, className: String, methodName: String, line: Int, msg: String?, throwable: Throwable?)

}