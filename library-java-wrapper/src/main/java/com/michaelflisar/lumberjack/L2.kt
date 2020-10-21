package com.michaelflisar.lumberjack

import com.michaelflisar.lumberjack.data.StackData
import timber.log.Timber

/*
 this class is meant for usage in JAVA only, as functions can't be inlined there
 and the callstack index to find the interesting callers class is different because of not inlining the functions!
 */
object L2 {

    @JvmStatic
    fun tag(tag: String): L2 {
        Timber.tag(tag)
        return L2
    }

    @JvmStatic
    fun callStackCorrection(value: Int): L2 {
        L.callStackCorrection(value)
        return L2
    }

    @JvmStatic
    fun v(t: Throwable?) = L2.log { Timber.v(t) }

    @JvmStatic
    fun v(t: Throwable? = null, message: String, vararg args: Any) = L2.log { Timber.v(t, message, *args) }

    @JvmStatic
    fun v(message: String, vararg args: Any) = L2.log { Timber.v(message, *args) }

    @JvmStatic
    fun v(message: String) = L2.log { Timber.v(message) }

    @JvmStatic
    fun d(t: Throwable?) = L2.log { Timber.d(t) }

    @JvmStatic
    fun d(t: Throwable? = null, message: String, vararg args: Any) = L2.log { Timber.d(t, message, *args) }

    @JvmStatic
    fun d(t: Throwable? = null, message: String) = L2.log { Timber.d(t, message) }

    @JvmStatic
    fun d(message: String, vararg args: Any) = L2.log { Timber.d(message, *args) }

    @JvmStatic
    fun d(message: String) = L2.log { Timber.d(message) }

    @JvmStatic
    fun i(t: Throwable?) = L2.log { Timber.i(t) }

    @JvmStatic
    fun i(t: Throwable? = null, message: String, vararg args: Any) = L2.log { Timber.i(t, message, *args) }

    @JvmStatic
    fun i(t: Throwable? = null, message: String) = L2.log { Timber.i(t, message) }

    @JvmStatic
    fun i(message: String, vararg args: Any) = L2.log { Timber.i(message, *args) }

    @JvmStatic
    fun i(message: String) = L2.log { Timber.i(message) }

    @JvmStatic
    fun w(t: Throwable?) = L2.log { Timber.w(t) }

    @JvmStatic
    fun w(t: Throwable? = null, message: String, vararg args: Any) = L2.log { Timber.w(t, message, *args) }

    @JvmStatic
    fun w(t: Throwable? = null, message: String) = L2.log { Timber.w(t, message) }

    @JvmStatic
    fun w(message: String, vararg args: Any) = L2.log { Timber.w(message, *args) }

    @JvmStatic
    fun w(message: String) = L2.log { Timber.w(message) }

    @JvmStatic
    fun e(t: Throwable?) = L2.log { Timber.e(t) }

    @JvmStatic
    fun e(t: Throwable? = null, message: String, vararg args: Any) = L2.log { Timber.e(t, message, *args) }

    @JvmStatic
    fun e(t: Throwable? = null, message: String) = L2.log { Timber.e(t, message) }

    @JvmStatic
    fun e(message: String, vararg args: Any) = L2.log { Timber.e(message, *args) }

    @JvmStatic
    fun e(message: String) = L2.log { Timber.e(message) }

    @JvmStatic
    fun wtf(t: Throwable?) = L2.log { Timber.wtf(t) }

    @JvmStatic
    fun wtf(t: Throwable? = null, message: String, vararg args: Any) = L2.log { Timber.wtf(t, message, *args) }

    @JvmStatic
    fun wtf(t: Throwable? = null, message: String) = L2.log { Timber.wtf(t, message) }

    @JvmStatic
    fun wtf(message: String, vararg args: Any) = L2.log { Timber.wtf(message, *args) }

    @JvmStatic
    fun wtf(message: String) = L2.log { Timber.wtf(message) }

    // --------------
    // helper function
    // --------------

    fun log(logBlock: () -> Unit) {
        if (L.enabled && Timber.treeCount() > 0) {
            L.callStackCorrection(4)
            if (L.packageNameFilter?.let { it.invoke(StackData.create(null, 0).className) } != false)
                logBlock()
        }
    }
}
