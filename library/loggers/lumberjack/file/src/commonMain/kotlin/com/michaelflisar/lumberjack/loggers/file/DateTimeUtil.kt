package com.michaelflisar.lumberjack.loggers.file

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal object DateTimeUtil {

    fun now() = dateTime(Clock.System.now().toEpochMilliseconds())

    fun dateTime(millis: Long): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(millis)
        val tz = TimeZone.currentSystemDefault()
        val dateTime = instant.toLocalDateTime(tz)
        return dateTime
    }

}