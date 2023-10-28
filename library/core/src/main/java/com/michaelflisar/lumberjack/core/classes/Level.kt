package com.michaelflisar.lumberjack.core.classes

import android.graphics.Color
import android.util.Log
import com.michaelflisar.lumberjack.core.classes.CoreUtil.isColorDark

private val COLOR_LIGHT_BLUE = Color.rgb(173, 216, 230)
private val COLOR_ORANGE = Color.rgb(255, 165, 0)
private val COLOR_LIGHT_RED = Color.rgb(255, 204, 203)

enum class Level(
    val priority: Int,
    val shortcut: String,
    val color: Int? = null,
    val colorDark: Int? = color
) {
    VERBOSE(Log.VERBOSE, "V"),
    DEBUG(Log.DEBUG, "D"),
    INFO(Log.INFO, "I", Color.BLUE, COLOR_LIGHT_BLUE),
    WARN(Log.WARN, "W", COLOR_ORANGE),
    ERROR(Log.ERROR, "E", Color.RED, COLOR_LIGHT_RED),
    WTF(Log.ASSERT, "WTF", Color.RED, COLOR_LIGHT_RED),
    NONE(-1, "-")
    ;

    fun getTitleColor(textColor: Int): Int {
        return (if (textColor.isColorDark()) color else colorDark) ?: textColor
    }

    fun getTextColor(textColor: Int): Int {
        val c = getTitleColor(textColor)
        return if (c == android.R.color.transparent)
            textColor
        else c
    }
}