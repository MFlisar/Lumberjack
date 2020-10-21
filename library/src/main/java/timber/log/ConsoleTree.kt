package timber.log

import android.annotation.SuppressLint
import android.util.Log
import com.michaelflisar.lumberjack.data.StackData

/**
 * Created by flisar on 17.01.2019.
 */

class ConsoleTree(
    private val appendClickableLink: Boolean = true
) : BaseTree() {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
        stackData: StackData
    ) {

        val fullMessage = if (appendClickableLink) appendLink(message, stackData) else message

        if (fullMessage.length < MAX_LOG_LENGTH) {
            logLine(priority, tag, fullMessage)
            return
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = fullMessage.length
        while (i < length) {
            var newline = fullMessage.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = Math.min(newline, i + MAX_LOG_LENGTH)
                val part = fullMessage.substring(i, end)
                logLine(priority, tag, part)
                i = end
            } while (i < newline)
            i++
        }
    }

    // tag is logged anyways, so we do NOT add it to the message!
    override fun formatLine(tag: String?, message: String) = "$message"

    @SuppressLint("LogNotTimber")
    private fun logLine(priority: Int, tag: String?, message: String) {
        val logMessage = formatLine(tag, message)
        if (priority == Log.ASSERT) {
            Log.wtf(tag, logMessage)
        } else {
            Log.println(priority, tag, logMessage)
        }
    }

    private fun appendLink(message: String, stackData: StackData): String {
        val link = stackData.link
        return "$message ($link)"
    }
}