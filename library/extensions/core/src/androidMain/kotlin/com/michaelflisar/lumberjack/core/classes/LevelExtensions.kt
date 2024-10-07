package com.michaelflisar.lumberjack.core.classes

import android.util.Log
import com.michaelflisar.lumberjack.core.classes.CoreUtil.isColorDark

val Level.priority: Int
    get() = when (this) {
        Level.VERBOSE -> Log.VERBOSE
        Level.DEBUG -> Log.DEBUG
        Level.INFO -> Log.INFO
        Level.WARN -> Log.WARN
        Level.ERROR -> Log.ERROR
        Level.WTF -> Log.ASSERT
        Level.NONE -> -1
    }

fun Level.getTitleColor(textColor: Int): Int {
    return (if (textColor.isColorDark()) color else colorDark) ?: textColor
}

fun Level.getTextColor(textColor: Int): Int {
    val c = getTitleColor(textColor)
    return if (c == android.R.color.transparent)
        textColor
    else c
}