package com.michaelflisar.lumberjack.loggers.console

import com.michaelflisar.lumberjack.implementation.platformPrintln
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.DefaultLumberjackFilter
import com.michaelflisar.lumberjack.implementation.classes.LumberjackFilter
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger

/**
 * Console logger that writes logs to the platform console and allows transforming the runtime tag.
 *
 * This logger is useful on platforms that produce a lot of internal logs (e.g. certain Android devices).
 * The `tagTransformer` function lets callers format, shorten, sanitize, prefix, or completely replace the
 * runtime `tag` before it is passed to the underlying platform print routine.
 *
 * @param minLogLevel minimum log level this logger outputs. Default: [Level.VERBOSE].
 * @param filter Lumberjack filter used to decide which logs are allowed. Default: [DefaultLumberjackFilter].
 * @param tagTransformer function called for every log with the runtime `tag` (may be `null`) and
 *        returning the final tag to use (or `null` to indicate no tag). The default is the identity
 *        function (`{ it }`) which leaves the runtime tag unchanged.
 *
 * Notes and recommendations:
 * - `tagTransformer` is invoked on every log call and therefore should be fast and side-effect free.
 * - If you want to include a fixed prefix/tag, you can capture it from the surrounding scope:
 *   `val logger = ConsoleLogger(tagTransformer = { tag -> if (tag != null) "FIXED-$tag" else "FIXED" })`
 * - The transformer may return `null` if no tag should be used.
 *
 * Note: The logger intentionally places `className` before the "(file:line)" part so IDEs/logcat
 * can make the filename:line clickable.
 */
class ConsoleLogger(
    override val minLogLevel: Level = Level.VERBOSE,
    override val filter: LumberjackFilter = DefaultLumberjackFilter,
    val tagTransformer: (tag: String?) -> String? = { it }
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
        platformPrintln(prefix, level, tagTransformer(tag), log)
    }

}