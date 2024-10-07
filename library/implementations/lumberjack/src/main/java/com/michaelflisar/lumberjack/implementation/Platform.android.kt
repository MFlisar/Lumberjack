package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.core.classes.priority

actual object Log {
    actual fun println(prefix: String, level: Level, tag: String?, log: String) {
        android.util.Log.println(level.priority, tag, log)
    }
}