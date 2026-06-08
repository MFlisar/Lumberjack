package com.michaelflisar.lumberjack.extensions.composeviewer

import com.michaelflisar.kmp.platformcontext.platformContext
import klip.SystemClipboard

internal actual suspend fun setClipboard(text: String) {
    val context = platformContext
    val clipboard = SystemClipboard(context)
    clipboard.set(text)
}