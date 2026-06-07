package com.michaelflisar.lumberjack.extensions.composeviewer

import klip.SystemClipboard

internal actual suspend fun setClipboard(text: String) {
    val clipboard = SystemClipboard()
    clipboard.set(text)
}
