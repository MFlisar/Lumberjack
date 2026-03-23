package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.classes.priority
import com.michaelflisar.lumberjack.implementation.classes.StackData

internal actual fun createStackData(throwable: Throwable, callStackIndex: Int): StackData {
    return StackData.createDefault(throwable, callStackIndex)
}

actual fun platformPrintln(prefix: String, level: Level, tag: String?, log: String) {
    android.util.Log.println(level.priority, tag, log)
}