package com.michaelflisar.lumberjack.loggers.timber.file

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.interfaces.IFileConverter
import dev.icerock.moko.parcelize.IgnoredOnParcel
import dev.icerock.moko.parcelize.Parcelize
import java.util.regex.Pattern

@Parcelize
object FileConverter: IFileConverter {

    @IgnoredOnParcel
    private val DATE_PATTERN: Pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$")

    override fun parseFile(lines: List<String>) : List<IFileConverter.Entry> {
        val entries = ArrayList<IFileConverter.Entry>()
        var lineNumber: Int = 1
        lines
            .forEach { line ->
                // Log Line looks like following:
                // 2000-01-01 00:00:00.000 INFO Some log
                // Exception Lines, ...
                if (line.length > 10 && DATE_PATTERN.matcher(line.substring(0, 10)).matches()) {
                    var date = ""
                    var level = Level.NONE
                    val parts = line.split(" ")
                    var log = line
                    if (parts.size > 3) {
                        date = parts[0] + " " + parts[1]
                        level = parts[2].replace("[", "").replace("]", "").let { marker ->
                            Level.values().find { it.shortcut == marker }
                        } ?: Level.NONE
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
    ) : String {

        // UNUSED

        return ""
    }
}