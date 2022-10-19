package com.michaelflisar.lumberjack

import com.michaelflisar.lumberjack.core.DefaultFormatter
import com.michaelflisar.lumberjack.data.StackData
import com.michaelflisar.lumberjack.interfaces.IFilter
import com.michaelflisar.lumberjack.interfaces.IFormatter
import timber.log.BaseTree
import timber.log.Timber

/*
logs LAZILY and at the callers place via kotlin inline functions
 */
@Suppress("NOTHING_TO_INLINE")
object L {

    // --------------
    // setup
    // --------------

    /*
     * if false, all logging is disabled
     */
    var enabled = true

    /*
     * if enabled, Lumberjack will try to find out a lambdas caller and append this info to the log tag
     * does not work perfectly, we would need to distinguish between lambdas called directly or by a coroutine and more...
     */
    internal val advancedLambdaLogging = false

    /*
     * provide a custom formatter to influence, how tags and class names are logged
     *
     * you can change the whole log prefix if desired; there's also an open default formatter
     * that you can use to extend your custom formatter from: [com.michaelflisar.lumberjack.core.DefaultFormatter]
     */
    var formatter: IFormatter = DefaultFormatter()

    /*
     * provide a custom filter to filter out logs based on content, tags, class names, ...
     *
     * by default nothing is filtered
     */
    var filter: IFilter? = null

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
/*
    fun callStackCorrection(value: Int): L {
        setCallStackCorrection(value)
        return L
    }
*/
    // --------------
    // log functions - lazy
    // --------------

    @JvmStatic
    fun v(t: Throwable, message: () -> String) = log(t, t) { Timber.v(t, message()) }

    @JvmStatic
    fun v(t: Throwable) = log(t, t) { Timber.v(t) }

    @JvmStatic
    fun v(message: () -> String) = log(null, Throwable()) { Timber.v(message()) }

    @JvmStatic
    fun d(t: Throwable, message: () -> String) = log(t, t) { Timber.d(t, message()) }

    @JvmStatic
    fun d(t: Throwable) = log(t, t) { Timber.d(t) }

    @JvmStatic
    fun d(message: () -> String) = log(null, Throwable()) { Timber.d(message()) }

    @JvmStatic
    fun i(t: Throwable, message: () -> String) = log(t, t) { Timber.i(t, message()) }

    @JvmStatic
    fun i(t: Throwable) = log(t, t) { Timber.i(t) }

    @JvmStatic
    fun i(message: () -> String) = log(null, Throwable()) { Timber.i(message()) }

    @JvmStatic
    fun w(t: Throwable, message: () -> String) = log(t, t) { Timber.w(t, message()) }

    @JvmStatic
    fun w(t: Throwable) = log(t, t) { Timber.w(t) }

    @JvmStatic
    fun w(message: () -> String) = log(null, Throwable()) { Timber.w(message()) }

    @JvmStatic
    fun e(t: Throwable, message: () -> String) = log(t, t) { Timber.e(t, message()) }

    @JvmStatic
    fun e(t: Throwable) = log(t, t) { Timber.e(t) }

    @JvmStatic
    fun e(message: () -> String) = log(null, Throwable()) { Timber.e(message()) }

    @JvmStatic
    fun wtf(t: Throwable, message: () -> String) = log(t, t) { Timber.wtf(t, message()) }

    @JvmStatic
    fun wtf(t: Throwable) = log(t, t) { Timber.wtf(t) }

    @JvmStatic
    fun wtf(message: () -> String) = log(null, Throwable()) { Timber.wtf(message()) }

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
    internal inline fun log(t: Throwable?, t2: Throwable, logBlock: () -> Unit) {
        if (enabled && Timber.treeCount() > 0) {
            val stackTrace = StackData(t?.stackTrace ?: t2.stackTrace, if (t == null) 1 else 0)
            if (filter?.isPackageNameEnabled(stackTrace.getCallingPackageName()) != false) {
                setStackTraceData(stackTrace)
                logBlock()
            }
        }
        return
    }

    /*
    internal fun setCallStackCorrection(correction: Int) {
        val forest = Timber.forest()
        for (tree in forest) {
            if (tree is BaseTree) {
                tree.setCallStackCorrection(correction)
            }
        }
    }
*/
    fun setStackTraceData(stackTraceData: StackData) {
        val forest = Timber.forest()
        for (tree in forest) {
            if (tree is BaseTree) {
                tree.setStackTrace(stackTraceData)
            }
        }
    }
}