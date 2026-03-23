package com.michaelflisar.lumberjack.loggers.file

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

internal object DateTimeUtil {

    fun now(): LocalTime {
        val now = Clock.System.now()
        return now.toLocalDateTime(TimeZone.currentSystemDefault()).time
    }

    fun dateTime(millis: Long): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(millis)
        val tz = TimeZone.currentSystemDefault()
        return instant.toLocalDateTime(tz)
    }

}