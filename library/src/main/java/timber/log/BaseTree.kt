package timber.log

import android.util.Log
import com.michaelflisar.lumberjack.L
import com.michaelflisar.lumberjack.data.StackData
import java.io.PrintWriter
import java.io.StringWriter

abstract class BaseTree : Timber.Tree() {

    // we overwrite all log functions because the base classes Timber.prepareLog is private and we need to make a small adjustment
    // to get correct line numbers for kotlin exceptions (only the IDE does convert the line limbers correctly based on a mapping table)

    override fun v(message: String?, vararg args: Any?) {
        prepareLog(
            Log.VERBOSE,
            null,
            message,
            args
        )
    }

    override fun v(t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            Log.VERBOSE,
            t,
            message,
            args
        )
    }

    override fun v(t: Throwable?) {
        prepareLog(Log.VERBOSE, t, null)
    }

    override fun d(message: String?, vararg args: Any?) {
        prepareLog(Log.DEBUG, null, message, args)
    }

    override fun d(t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            Log.DEBUG,
            t,
            message,
            args
        )
    }

    override fun d(t: Throwable?) {
        prepareLog(Log.DEBUG, t, null)
    }

    override fun i(message: String?, vararg args: Any?) {
        prepareLog(Log.INFO, null, message, args)
    }

    override fun i(t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            Log.INFO,
            t,
            message,
            args
        )
    }

    override fun i(t: Throwable?) {
        prepareLog(Log.INFO, t, null)
    }

    override fun w(message: String?, vararg args: Any?) {
        prepareLog(Log.WARN, null, message, args)
    }

    override fun w(t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            Log.WARN,
            t,
            message,
            args
        )
    }

    override fun w(t: Throwable?) {
        prepareLog(Log.WARN, t, null)
    }

    override fun e(message: String?, vararg args: Any?) {
        prepareLog(Log.ERROR, null, message, args)
    }

    override fun e(t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            Log.ERROR,
            t,
            message,
            args
        )
    }

    override fun e(t: Throwable?) {
        prepareLog(Log.ERROR, t, null)
    }

    override fun wtf(message: String?, vararg args: Any?) {
        prepareLog(
            Log.ASSERT,
            null,
            message,
            args
        )
    }

    override fun wtf(t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            Log.ASSERT,
            t,
            message,
            args
        )
    }

    override fun wtf(t: Throwable?) {
        prepareLog(Log.ASSERT, t, null)
    }

    override fun log(priority: Int, message: String?, vararg args: Any?) {
        prepareLog(
            priority,
            null,
            message,
            args
        )
    }

    override fun log(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {
        prepareLog(
            priority,
            t,
            message,
            args
        )
    }

    override fun log(priority: Int, t: Throwable?) {
        prepareLog(priority, t, null)
    }

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return tag == null || (L.tagNameFilter?.invoke(tag) ?: true)
    }

    // copied from Timber.Tree because it's private in the base class
    private fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    // --------------------
    // custom code
    // --------------------

    private fun prepareLog(priority: Int, t: Throwable?, message: String?, vararg args: Any?) {

        // custom: we create a stack data object
        val callStackCorrection = getCallStackCorrection() ?: 0
        val stackData = StackData(t, CALL_STACK_INDEX + callStackCorrection)

        // Consume tag even when message is not loggable so that next message is correctly tagged.
        @Suppress("NAME_SHADOWING") var message = message
        val tag = getTag(stackData)
        if (!isLoggable(tag, priority)) {
            return
        }
        if (message != null && message.isEmpty()) {
            message = null
        }
        if (message == null) {
            if (t == null) {
                return  // Swallow message if it's null and there's no throwable.
            }
            message = getStackTraceString(t)
        } else {
            if (args != null && args.isNotEmpty()) {
                message = String.format(message, *args)
            }
            if (t != null) {
                message += "${getStackTraceString(t)}".trimIndent()
            }
        }
        log(priority, tag, message, t, stackData)
    }

    final override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) { /* empty, we use our own function with StackData */
    }

    abstract fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
        stackData: StackData
    )

    // --------------------
    // custom code - extended tag
    // --------------------

    protected open fun formatLine(tag: String?, message: String) = "[$tag]: $message"

    // --------------------
    // custom code - extended tag
    // --------------------

    private fun getTag(stackData: StackData): String? {

        // 1) get custom tag if one exists
        val customTag = super.getTag()

        // 2) create a custom tag for the logs
        return if (customTag != null) {
            "[<$customTag> ${stackData.getStackTag()}]"
        } else {
            "[${stackData.getStackTag()}]"
        }
    }

    // --------------------
    // custom code - callstack depth
    // --------------------

    companion object {
        internal const val CALL_STACK_INDEX = 4
    }

    private val callStackCorrection = ThreadLocal<Int>()
    private fun getCallStackCorrection(): Int? {
        val correction = callStackCorrection.get()
        if (correction != null) {
            callStackCorrection.remove()
        }
        return correction
    }

    internal fun setCallStackCorrection(value: Int) {
        callStackCorrection.set(value)
    }
}