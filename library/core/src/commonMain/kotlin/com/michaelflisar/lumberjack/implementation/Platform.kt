package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.classes.StackData

internal expect fun createStackData(throwable: Throwable, callStackIndex: Int): StackData
expect fun platformPrintln(prefix: String, level: Level, tag: String?, log: String)
