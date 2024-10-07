package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level

actual object Log {

    actual fun println(prefix: String, level: Level, tag: String?, log: String) {
        println("${prefix.let { "$it " }}[${level.shortcut}]${tag?.let { " <$it>" } ?: ""} $log")
    }
}