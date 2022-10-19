package com.michaelflisar.lumberjack.demo

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

object Util {

    fun isNightMode(context: Context): Boolean {
        val defaultNightMode = AppCompatDelegate.getDefaultNightMode()
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            return true
        }
        if (defaultNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            return false
        }
        val currentNightMode: Int = (context.resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }
}