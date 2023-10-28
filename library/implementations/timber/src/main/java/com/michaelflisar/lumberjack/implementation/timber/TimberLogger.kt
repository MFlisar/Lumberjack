package com.michaelflisar.lumberjack.implementation.timber

import com.michaelflisar.lumberjack.core.AbstractLogger
import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.timber.core.DefaultFormatter
import com.michaelflisar.lumberjack.implementation.timber.data.StackData
import com.michaelflisar.lumberjack.implementation.timber.interfaces.IFilter
import com.michaelflisar.lumberjack.implementation.timber.interfaces.IFormatter
import timber.log.BaseTree
import timber.log.Timber

/*
logs LAZILY and at the callers place via kotlin inline functions
 */
@Suppress("NOTHING_TO_INLINE")
object TimberLogger : AbstractLogger() {

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
    // AbstractLogger
    // --------------

    override fun tag(tag: String): TimberLogger {
        Timber.tag(tag)
        return this
    }

    override fun callStackCorrection(value: Int): TimberLogger {
        setCallStackCorrection(value)
        return this
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun doLog(level: Level, message: String?, t: Throwable?, t2: Throwable) {
        if (enabled && Timber.treeCount() > 0) {
            val stackTrace = StackData(t ?: t2, if (t == null) 1 else 0)
            if (filter?.isPackageNameEnabled(stackTrace.getCallingPackageName()) != false) {
                setStackTraceData(stackTrace)
                if (t == null)
                    Timber.log(level.priority, message)
                else
                    Timber.log(level.priority, t, message)
            }
        }
    }

    // --------------
    // helper function
    // --------------

    /** @suppress */
    @PublishedApi
    internal inline fun log(t: Throwable?, t2: Throwable, logBlock: () -> Unit) {
        if (enabled && Timber.treeCount() > 0) {
            val stackTrace = StackData(t ?: t2, if (t == null) 1 else 0)
            if (filter?.isPackageNameEnabled(stackTrace.getCallingPackageName()) != false) {
                setStackTraceData(stackTrace)
                logBlock()
            }
        }
        return
    }

    private fun setCallStackCorrection(correction: Int) {
        val forest = Timber.forest()
        for (tree in forest) {
            if (tree is BaseTree) {
                tree.setCallStackCorrection(correction)
            }
        }
    }

    fun setStackTraceData(stackTraceData: StackData) {
        val forest = Timber.forest()
        for (tree in forest) {
            if (tree is BaseTree) {
                tree.setStackTrace(stackTraceData)
            }
        }
    }
}