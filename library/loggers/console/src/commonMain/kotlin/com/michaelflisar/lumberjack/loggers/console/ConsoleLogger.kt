package com.michaelflisar.lumberjack.loggers.console

import com.michaelflisar.lumberjack.implementation.platformPrintln
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.DefaultLumberjackFilter
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger

/**
 * Console logger implementation that prints logs to the platform console with an optional fixed tag.
 *
 * This logger is useful on devices that produce a lot of internal logs (for example certain Samsung
 * models). Using a fixed tag makes it easier to filter important logs (e.g. in logcat: "package:mine tag:TVRC").
 *
 * @param minLogLevel Minimum log level this logger will output. Defaults to [Level.VERBOSE].
 * @param filter Lumberjack filter used to decide which logs are allowed. Defaults to [DefaultLumberjackFilter] which does not filter out anything.
 * @param fixTag Optional constant tag used as a stable identifier for logs emitted by this logger.
 *        When present, it helps to reliably filter app logs on devices that emit many internal messages.
 * @param combineFixTagWithLocalTag When true and both [fixTag] and a runtime `tag` are available,
 *        the logger combines them as "$fixTag-$tag" so you keep a constant filtering token while
 *        preserving the dynamic/local tag information. When false, the logger uses [fixTag] (if set)
 *        or the runtime `tag` otherwise.
 *
 * Note: The logger intentionally places `className` before the "(file:line)" part so IDEs/logcat
 * can make the filename:line clickable.
 */
class ConsoleLogger(
    override val minLogLevel: Level = Level.VERBOSE,
    override val filter: LumberjackFilter = DefaultLumberjackFilter,
    val fixTag: String? = null,
    val combineFixTagWithLocalTag: Boolean = true
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
        // className must occur somewhere before filename:line part to make that data clickable
        val prefix = className
        val link = "($fileName:$line)"
        val log = listOfNotNull(
            msg,
            link.takeIf { throwable == null },
            throwable?.stackTraceToString()?.let { "\n$it" }
        ).joinToString(" ")
        val t = if (combineFixTagWithLocalTag && fixTag != null && tag != null) {
            "$fixTag-$tag"
        } else (fixTag ?: tag)
        platformPrintln(prefix, level, t, log)
    }

}