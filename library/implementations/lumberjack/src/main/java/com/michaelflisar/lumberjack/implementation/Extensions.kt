package com.michaelflisar.lumberjack.implementation

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.implementation.interfaces.ILumberjackLogger

fun L.plant(implementation: ILumberjackLogger) {
    LumberjackLogger.register(implementation)
}

fun L.uproot(implementation: ILumberjackLogger) {
    LumberjackLogger.unregister(implementation)
}

fun L.uprootAll() {
    LumberjackLogger.unregisterAll()
}

fun L.enable(enabled: Boolean) {
    LumberjackLogger.enable(enabled)
}