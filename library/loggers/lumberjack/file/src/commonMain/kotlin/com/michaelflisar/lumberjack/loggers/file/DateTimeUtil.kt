package com.michaelflisar.lumberjack.loggers.file

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal object DateTimeUtil {

    @OptIn(ExperimentalTime::class)
    fun now(): LocalTime {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.time
    }

    @OptIn(ExperimentalTime::class)
    fun dateTime(millis: Long): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(millis)
        val tz = TimeZone.currentSystemDefault()
        val dateTime = instant.toLocalDateTime(tz)
        return dateTime
    }

}