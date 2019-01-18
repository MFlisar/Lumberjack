package timber.log

import android.util.Log
import com.michaelflisar.lumberjack.data.StackData

/**
 * Created by flisar on 17.01.2019.
 */

class ConsoleTree(val appendClickableLink: Boolean = true) : BaseTree() {

    companion object {
        private const val MAX_LOG_LENGTH = 4000
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        val fullMessage = if (appendClickableLink) appendLink(message) else message

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

    private fun logLine(priority: Int, tag: String?, message: String) {
        // tag is logged anyways, so we do NOT add it to the message!
        val logMessage = message//formatLine(tag, message)

        if (priority == Log.ASSERT) {
            Log.wtf(tag, logMessage)
        } else {
            Log.println(priority, tag, logMessage)
        }
    }

    private fun appendLink(message: String): String {
        val link = lastStackData.getLink()
        val lines = message.split("\r\n|\r|\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (lines.size <= 1) {
            return message + " (" + link + ")"
        }
        // this makes sure that links always works, like for example if pretty print for collections is enabled
        else {
            lines[0] = lines[0] + " (" + link + ")"
            val builder = StringBuilder()
            for (s in lines)
                builder.append(s).append("\n")
            return builder.toString()
        }
    }
}