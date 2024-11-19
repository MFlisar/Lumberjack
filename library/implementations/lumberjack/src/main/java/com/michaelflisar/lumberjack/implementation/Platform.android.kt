package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.classes.priority

actual fun platformPrintln(prefix: String, level: Level, tag: String?, log: String) {
    android.util.Log.println(level.priority, tag, log)
}