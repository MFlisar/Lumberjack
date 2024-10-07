package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level

expect object Log {
    fun println(prefix: String, level: Level, tag: String?, log: String)
}


