package com.michaelflisar.lumberjack.extensions.composeviewer

import com.michaelflisar.kmp.platformcontext.PlatformContextProvider
import klip.SystemClipboard

internal actual suspend fun setClipboard(text: String) {
    val context = PlatformContextProvider.get()
    val clipboard = SystemClipboard(context)
    clipboard.set(text)
}