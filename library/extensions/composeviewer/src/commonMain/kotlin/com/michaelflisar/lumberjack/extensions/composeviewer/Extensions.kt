package com.michaelflisar.lumberjack.extensions.composeviewer

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.michaelflisar.lumberjack.core.classes.Level

@Composable
fun Level.getColor(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) (
            colorDark?.let { Color(it) } ?:
            color?.let { Color(it) } ?:
            Color.Unspecified
    ) else (
            color?.let { Color(it) } ?:
            Color.Unspecified
    )
}