package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level

actual fun platformPrintln(prefix: String, level: Level, tag: String?, log: String) {
    println("${if (prefix.isEmpty()) "" else "$prefix "}[${level.shortcut}]${tag?.let { " <$it>" } ?: ""} $log")
}