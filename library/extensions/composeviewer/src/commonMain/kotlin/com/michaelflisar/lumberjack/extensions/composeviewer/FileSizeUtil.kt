package com.michaelflisar.lumberjack.extensions.composeviewer

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

object FileSizeUtil {

    fun humanReadableBytes(bytes: Long): String {
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = if (bytes == 0L) 0 else (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(bytes / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}