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
     * provide a filter for package names - you will get the callers package name
     */
    var packageNameFilter: ((packageName: String) -> Boolean)? = null

    /*
     * provide a filter for tags
     */
    var tagNameFilter: ((tags: String) -> Boolean)? = null

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

    inline fun v(t: Throwable? = null, message: () -> String) = log(t) { Timber.v(t, message()) }

    inline fun v(t: Throwable?) = log(t) { Timber.v(t) }

    inline fun v(message: () -> String) = log(null) { Timber.v(message()) }

    inline fun d(t: Throwable? = null, message: () -> String) = log(t) { Timber.d(t, message()) }

    inline fun d(t: Throwable?) = log(t) { Timber.d(t) }

    inline fun d(message: () -> String) = log(null) { Timber.d(message()) }

    inline fun i(t: Throwable? = null, message: () -> String) = log(t) { Timber.i(t, message()) }

    inline fun i(t: Throwable?) = log(t) { Timber.i(t) }

    inline fun i(message: () -> String) = log(null) { Timber.i(message()) }

    inline fun w(t: Throwable? = null, message: () -> String) = log(t) { Timber.w(t, message()) }

    inline fun w(t: Throwable?) = log(t) { Timber.w(t) }

    inline fun w(message: () -> String) = log(null) { Timber.w(message()) }

    inline fun e(t: Throwable? = null, message: () -> String) = log(t) { Timber.e(t, message()) }

    inline fun e(t: Throwable?) = log(t) { Timber.e(t) }

    inline fun e(message: () -> String) = log(null) { Timber.e(message()) }

    inline fun wtf(t: Throwable? = null, message: () -> String) = log(t) { Timber.wtf(t, message()) }

    inline fun wtf(t: Throwable?) = log(t) { Timber.wtf(t) }

    inline fun wtf(message: () -> String) = log(null) { Timber.wtf(message()) }

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
    internal inline fun log(t: Throwable?, logBlock: () -> Unit) {
        if (enabled && Timber.treeCount() > 0) {
            if (packageNameFilter?.let { it.invoke(StackData.create(t, 0).className) } != false)
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