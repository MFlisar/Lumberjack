package com.michaelflisar.lumberjack.demo.classes

object DemoLibraryWithInternalLogger {

    var logger: ((msg: String) -> Unit)? = null

    fun run() {
        logger?.invoke("TEST-LOG - Debug Message from library...")
    }
}