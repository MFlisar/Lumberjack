package com.michaelflisar.lumberjack.loggers.file

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import com.michaelflisar.parcelize.IgnoredOnParcel
import com.michaelflisar.parcelize.Parcelize
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

@Parcelize
object FileConverter : IFileConverter {

    // "yyyy-MM-dd HH:mm:ss.SSS"
    @IgnoredOnParcel
    private val timeFormatter = LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        day(padding = Padding.ZERO)
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
        char('.')
        secondFraction(3)
    }

    override fun parseFile(lines: List<String>): List<IFileConverter.Entry> {
        val entries = ArrayList<IFileConverter.Entry>()
        var lineNumber: Int = 1
        lines
            .forEach { line ->
                // Log Line looks like following:
                // [D] 2023-10-27 20:54:01.919 [MainActivity.kt:88]: TEST-LOG - Verbose log...
                // Exception Lines, ...
                if (line.startsWith("[")) {
                    var date = ""
                    var level = Level.NONE
                    val parts = line.split(" ")
                    var log = line
                    if (parts.size > 3 && parts[0].startsWith("[")) {
                        level = parts[0].replace("[", "").replace("]", "").let { shortcut ->
                            Level.entries.find { it.shortcut == shortcut }
                        } ?: Level.NONE
                        date = parts[1] + " " + parts[2]
                        log = parts.drop(3).joinToString(" ")
                    }
                    entries.add(IFileConverter.Entry(lineNumber++, mutableListOf(log), level, date))
                } else {
                    // append line to last entry
                    entries.lastOrNull()?.lines?.add(line)
                }
            }
        return entries
    }

    override fun formatLog(
        level: Level,
        tag: String?,
        time: Long,
        fileName: String,
        className: String,
        methodName: String,
        line: Int,
        msg: String?,
        throwable: Throwable?
    ): String {

        val date = DateTimeUtil.dateTime(time)

        // Result
        // [D] 2023-10-27 20:54:01.919 [MainActivity.kt:88]: TEST-LOG - Verbose log...

        val text =
            // 1) Log Level
            "[${level.shortcut}] " +
                    // 2) time "2023-10-27 20:23:02.422" to the text
                    timeFormatter.format(date) +
                    // 3) [MainActivity:26 onCreate]: Main activity created
                    " [${fileName}:${line}]: " + (msg ?: "") +
                    // 4) eventually append throwable stackttrace
                    (throwable?.stackTraceToString()?.let { "\n$it" } ?: "")

        return text
    }
}