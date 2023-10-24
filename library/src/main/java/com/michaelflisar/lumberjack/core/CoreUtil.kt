package com.michaelflisar.lumberjack.core

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color

object CoreUtil {

    fun getRealSubject(context: Context, subject: String): String {
        return context.getAppVersionName()?.let { "$subject (v$it)" } ?: subject
    }

    private fun Context.getAppVersionName(): String? {
        return try {
            val info = packageManager.getPackageInfo(packageName, 0)
            info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    internal fun Int.isColorDark(): Boolean {
        val darkness: Double = this.getDarknessFactor()
        return darkness >= 0.5
    }

    private fun Int.getDarknessFactor(): Double {
        return 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    }
}