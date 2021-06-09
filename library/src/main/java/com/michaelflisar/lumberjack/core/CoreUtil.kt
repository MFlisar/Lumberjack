package com.michaelflisar.lumberjack.core

import android.content.Context
import android.content.pm.PackageManager

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
}