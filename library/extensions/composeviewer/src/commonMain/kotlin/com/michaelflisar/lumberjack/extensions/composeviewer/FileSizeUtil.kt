package com.michaelflisar.lumberjack.extensions.composeviewer

//import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

object FileSizeUtil {

    fun humanReadableBytes(bytes: Long): String {
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = if (bytes == 0L) 0 else (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return formatNumberManually(bytes / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    // manual version of DecimalFormat("#,##0.#")
    fun formatNumberManually(value: Double): String {
        //return DecimalFormat("#,##0.#").format(value)
        val integerPart = value.toInt()
        val decimalPart = ((value - integerPart) * 10).toInt() // Eine Dezimalstelle
        val integerPartWithGrouping = integerPart.toString().reversed().chunked(3).joinToString(",").reversed()
        return if (decimalPart > 0) "$integerPartWithGrouping.$decimalPart" else integerPartWithGrouping
    }
}