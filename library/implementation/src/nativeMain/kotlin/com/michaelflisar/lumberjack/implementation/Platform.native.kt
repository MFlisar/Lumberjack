package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.StackData

internal actual fun createStackData(throwable: Throwable, callStackIndex: Int): StackData {
    return StackData.createNative(throwable, callStackIndex)
}

actual fun platformPrintln(prefix: String, level: Level, tag: String?, log: String) {
    println("${if (prefix.isEmpty()) "" else "$prefix "}[${level.shortcut}]${tag?.let { " <$it>" } ?: ""} $log")
}