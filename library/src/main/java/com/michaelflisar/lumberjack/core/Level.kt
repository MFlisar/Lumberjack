package com.michaelflisar.lumberjack.core

import android.graphics.Color

enum class Level(
    val level: Int,
    private val useDefaultTextColor: Boolean,
    private val color: Int
    ) {
    TRACE(0, true, -1),
    DEBUG(1, true, -1),
    INFO(2, true, -1),
    WARN(3, false, Color.parseColor("#FFA500") /* orange */),
    ERROR(4, false, Color.RED),
    UNKNOWN(-1, false, android.R.color.transparent)
    ;

    fun getTitleColor(textColor: Int): Int {
        return if (useDefaultTextColor) textColor else color
    }

    fun getTextColor(textColor: Int): Int {
        val c = getTitleColor(textColor)
        return if (!useDefaultTextColor && c == android.R.color.transparent)
            textColor
        else c
    }
}