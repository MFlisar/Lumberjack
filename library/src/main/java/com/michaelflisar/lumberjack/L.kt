package com.michaelflisar.lumberjack

import timber.log.Timber

/*
logs LAZILY and at the callers place via kotlin inline functions
 */
@Suppress("NOTHING_TO_INLINE")
object L {

    // --------------
    // setup
    // public fields because fields are accessed in inline functions
    // --------------

    var enabled = true

    // --------------
    // special functions
    // --------------

    private val tag = ThreadLocal<String>()

    fun logIf(block: () -> Boolean): L? {
        if (block()) {
            return L
        } else {
            return null
        }
    }

    fun tag(tag: String): L {
        Timber.tag(tag)
        return L
    }

    // --------------
    // log functions
    // --------------

    @JvmStatic
    inline fun v(t: Throwable? = null, message: () -> String, vararg args: Any) = log { Timber.v(t, message(), args) }

    @JvmStatic
    inline fun v(t: Throwable? = null, message: () -> String) = log { Timber.v(t, message()) }

    @JvmStatic
    inline fun v(t: Throwable?) = log { Timber.v(t) }

    @JvmStatic
    inline fun v(message: () -> String, vararg args: Any) = log { Timber.v(message(), args) }

    @JvmStatic
    inline fun v(message: () -> String) = log { Timber.v(message()) }

    @JvmStatic
    inline fun d(t: Throwable? = null, message: () -> String, vararg args: Any) = log { Timber.d(t, message(), args) }

    @JvmStatic
    inline fun d(t: Throwable? = null, message: () -> String) = log { Timber.d(t, message()) }

    @JvmStatic
    inline fun d(t: Throwable?) = log { Timber.d(t) }

    @JvmStatic
    inline fun d(message: () -> String, vararg args: Any) = log { Timber.d(message(), args) }

    @JvmStatic
    inline fun d(message: () -> String) = log { Timber.d(message()) }

    @JvmStatic
    inline fun i(t: Throwable? = null, message: () -> String, vararg args: Any) = log { Timber.i(t, message(), args) }

    @JvmStatic
    inline fun i(t: Throwable? = null, message: () -> String) = log { Timber.i(t, message()) }

    @JvmStatic
    inline fun i(t: Throwable?) = log { Timber.i(t) }

    @JvmStatic
    inline fun i(message: () -> String, vararg args: Any) = log { Timber.i(message(), args) }

    @JvmStatic
    inline fun i(message: () -> String) = log { Timber.i(message()) }

    @JvmStatic
    inline fun w(t: Throwable? = null, message: () -> String, vararg args: Any) = log { Timber.w(t, message(), args) }

    @JvmStatic
    inline fun w(t: Throwable? = null, message: () -> String) = log { Timber.w(t, message()) }

    @JvmStatic
    inline fun w(t: Throwable?) = log { Timber.w(t) }

    @JvmStatic
    inline fun w(message: () -> String, vararg args: Any) = log { Timber.w(message(), args) }

    @JvmStatic
    inline fun w(message: () -> String) = log { Timber.w(message()) }

    @JvmStatic
    inline fun e(t: Throwable? = null, message: () -> String, vararg args: Any) = log { Timber.e(t, message(), args) }

    @JvmStatic
    inline fun e(t: Throwable? = null, message: () -> String) = log { Timber.e(t, message()) }

    @JvmStatic
    inline fun e(t: Throwable?) = log { Timber.e(t) }

    @JvmStatic
    inline fun e(message: () -> String, vararg args: Any) = log { Timber.e(message(), args) }

    @JvmStatic
    inline fun e(message: () -> String) = log { Timber.e(message()) }

    @JvmStatic
    inline fun wtf(t: Throwable? = null, message: () -> String, vararg args: Any) = log { Timber.wtf(t, message(), args) }

    @JvmStatic
    inline fun wtf(t: Throwable? = null, message: () -> String) = log { Timber.wtf(t, message()) }

    @JvmStatic
    inline fun wtf(t: Throwable?) = log { Timber.wtf(t) }

    @JvmStatic
    inline fun wtf(message: () -> String, vararg args: Any) = log { Timber.wtf(message(), args) }

    @JvmStatic
    inline fun wtf(message: () -> String) = log { Timber.wtf(message()) }

    // --------------
    // timber forward functions
    // --------------

    @JvmStatic
    inline fun asTree(): Timber.Tree = Timber.asTree()

    @JvmStatic
    inline fun plant(tree: Timber.Tree) = Timber.plant(tree)

    @JvmStatic
    inline fun uproot(tree: Timber.Tree) = Timber.uproot(tree)

    @JvmStatic
    inline fun uprootAll() = Timber.uprootAll()

    // --------------
    // helper function
    // --------------

    /** @suppress */
    @PublishedApi
    internal inline fun log(logBlock: () -> Unit) {
        if (enabled && Timber.treeCount() > 0) {
            logBlock()
        }
    }
}