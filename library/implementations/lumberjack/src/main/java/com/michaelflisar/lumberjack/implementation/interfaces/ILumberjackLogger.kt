package com.michaelflisar.lumberjack.implementation.interfaces

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter

interface ILumberjackLogger {

    /*
     * if false, all logging is disabled
     */
    var enabled: Boolean

    /*
    * provide a filter to filter out logs based on content, tags, class names, ...
    */
    val filter: LumberjackFilter

    fun log(level: Level, tag: String?, time: Long, fileName: String, className: String, methodName: String, line: Int, msg: String?, throwable: Throwable?)

}