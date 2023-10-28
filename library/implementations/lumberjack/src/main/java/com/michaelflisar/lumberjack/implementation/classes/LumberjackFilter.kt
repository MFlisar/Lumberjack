package com.michaelflisar.lumberjack.implementation.classes

import com.michaelflisar.lumberjack.core.classes.Level

typealias LumberjackFilter = (level: Level, tag: String?, time: Long, fileName: String, className: String, methodName: String, line: Int, msg: String?, throwable: Throwable?) -> Boolean

/**
 * this filter does not filter out anything
 */
object DefaultLumberjackFilter : LumberjackFilter {

    override fun invoke(
        level: Level,
        tag: String?,
        time: Long,
        fileName: String,
        className: String,
        methodName: String,
        line: Int,
        msg: String?,
        throwable: Throwable?
    ): Boolean {
        return true
    }
}