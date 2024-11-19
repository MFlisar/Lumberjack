package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level

actual fun platformPrintln(prefix: String, level: Level, tag: String?, log: String) {
    println("${prefix.let { "$it " }}[${level.shortcut}]${tag?.let { " <$it>" } ?: ""} $log")
}