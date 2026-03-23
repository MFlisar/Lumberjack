package com.michaelflisar.lumberjack.core.classes

import android.graphics.Color

object CoreUtil {

    internal fun Int.isColorDark(): Boolean {
        val darkness: Double = this.getDarknessFactor()
        return darkness >= 0.5
    }

    private fun Int.getDarknessFactor(): Double {
        return 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    }
}