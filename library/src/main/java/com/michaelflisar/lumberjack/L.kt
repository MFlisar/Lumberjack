package com.michaelflisar.lumberjack

import com.michaelflisar.lumberjack.data.StackData
import timber.log.BaseTree
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

    /*
     * if false, all logging is disabled
     */
    var enabled = true

    /*
     * provide a filter for stacks - you will get the full stack trace package name
     */
    var packageNameFilter: ((String) -> Boolean)? = null

    // --------------
    // special functions
    // --------------

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

    fun callStackCorrection(value: Int): L {
        setCallStackCorrection(value)
        return L
    }

    // --------------
    // log functions - lazy
    // --------------

    inline fun v(t: Throwable? = null, message: () -> String) = log { Timber.v(t, message()) }

    inline fun v(t: Throwable?) = log { Timber.v(t) }

    inline fun v(message: () -> String) = log { Timber.v(message()) }

    inline fun d(t: Throwable? = null, message: () -> String) = log { Timber.d(t, message()) }

    inline fun d(t: Throwable?) = log { Timber.d(t) }

    inline fun d(message: () -> String) = log { Timber.d(message()) }

    inline fun i(t: Throwable? = null, message: () -> String) = log { Timber.i(t, message()) }

    inline fun i(t: Throwable?) = log { Timber.i(t) }

    inline fun i(message: () -> String) = log { Timber.i(message()) }

    inline fun w(t: Throwable? = null, message: () -> String) = log { Timber.w(t, message()) }

    inline fun w(t: Throwable?) = log { Timber.w(t) }

    inline fun w(message: () -> String) = log { Timber.w(message()) }

    inline fun e(t: Throwable? = null, message: () -> String) = log { Timber.e(t, message()) }

    inline fun e(t: Throwable?) = log { Timber.e(t) }

    inline fun e(message: () -> String) = log { Timber.e(message()) }

    inline fun wtf(t: Throwable? = null, message: () -> String) = log { Timber.wtf(t, message()) }

    inline fun wtf(t: Throwable?) = log { Timber.wtf(t) }

    inline fun wtf(message: () -> String) = log { Timber.wtf(message()) }

    // --------------
    // timber forward functions
    // --------------

    inline fun asTree(): Timber.Tree = Timber.asTree()

    inline fun plant(tree: Timber.Tree) = Timber.plant(tree)

    inline fun uproot(tree: Timber.Tree) = Timber.uproot(tree)

    inline fun uprootAll() = Timber.uprootAll()

    // --------------
    // helper function
    // --------------

    /** @suppress */
    @PublishedApi
    internal inline fun log(logBlock: () -> Unit) {
        if (enabled && Timber.treeCount() > 0) {
            if (packageNameFilter?.let { it.invoke(StackData.create(0).getStackTag()) } != false)
                logBlock()
        }
    }

    internal fun setCallStackCorrection(correction: Int) {
        val forest = Timber.forest()
        for (tree in forest) {
            if (tree is BaseTree) {
                tree.setCallStackCorrection(correction)
            }
        }
    }
}