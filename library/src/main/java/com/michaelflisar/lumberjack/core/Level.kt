package com.michaelflisar.lumberjack.core

import android.graphics.Color

enum class Level(
    val level: Int,
    private val color: Int? = null,
    private val marker: String? = null
) {

    TRACE(0),
    DEBUG(1),
    INFO(2, Color.BLUE),
    WARN(3, Color.parseColor("#FFA500") /* orange */),
    ERROR(4, Color.RED),
    WTF(5, Color.RED, "WTF-ERROR"),
    UNKNOWN(-1, android.R.color.transparent)
    ;

    fun getTitleColor(textColor: Int): Int {
        return color ?: textColor
    }

    fun getTextColor(textColor: Int): Int {
        val c = getTitleColor(textColor)
        return if (c == android.R.color.transparent)
            textColor
        else c
    }

    fun getFileMarker() = marker ?: name
}