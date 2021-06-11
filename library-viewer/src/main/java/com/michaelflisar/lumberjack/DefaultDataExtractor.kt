package com.michaelflisar.lumberjack

import com.michaelflisar.lumberjack.interfaces.IDataExtractor
import com.michaelflisar.lumberjack.core.Level
import kotlinx.parcelize.Parcelize

@Parcelize
object DefaultDataExtractor : IDataExtractor {

    override fun extract(line: Int, fullLogLine: String): IDataExtractor.Data? {
        // default log line looks like following:
        // 2000-01-01 00:00:00.000 INFO Some log
        // => 23 chars (including 1 space) + 2nd space + TAG + 3rd space + rest
        if (fullLogLine.trim().isNotEmpty()) {
            var date = ""
            var level = Level.UNKNOWN
            var log = ""
            if (fullLogLine.count { it == ' ' } > 3) {
                val ind1 = fullLogLine.indexOf(' ')
                val ind2 = fullLogLine.indexOf(' ', ind1 + 1)
                val ind3 = fullLogLine.indexOf(' ', ind2 + 1)
                val levelString = fullLogLine.substring(ind2, ind3).trim()
                date = fullLogLine.substring(0, ind2).trim()
                level = Level.values().find { it.getFileMarker() == levelString }
                    ?: Level.UNKNOWN

                log = fullLogLine.substring(ind3).trimStart()

            }
            return IDataExtractor.Data(line, fullLogLine, level, date, log)
        }
        return null
    }
}