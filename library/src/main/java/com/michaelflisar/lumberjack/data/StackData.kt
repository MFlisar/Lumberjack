package com.michaelflisar.lumberjack.data

import java.util.regex.Pattern


class StackData(private val className: String, private val simpleFileName: String, private val methodName: String, private val line: Int) {

    companion object {
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

        fun create(callStackIndex: Int): StackData {
            val stackTrace = Throwable().stackTrace
            if (stackTrace.size <= callStackIndex + 1) {
                throw IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using proguard?")
            }
            val element = stackTrace[callStackIndex + 1]
            var tag = element.className
            val m = ANONYMOUS_CLASS.matcher(tag)
            if (m.find()) {
                tag = m.replaceAll("")
            }
            return StackData(tag, element.fileName, element.methodName, element.lineNumber)
        }
    }

    val simpleClassName: String

    init {
        simpleClassName = className.substring(className.lastIndexOf('.') + 1)
    }

    fun getStackTag() = "$simpleClassName:$line $methodName"

    fun getLink() = "$simpleFileName:$line"

    fun appendLink(source: String): String {
        val lines = source.split("\r\n|\r|\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (lines.size <= 1)
            return "$source (${getLink()})"
        else {
            lines[0] = lines[0] + " (" + getLink() + ")"
            val builder = StringBuilder()
            for (s in lines)
                builder.append(s).append("\n")
            return builder.toString()
        }// this makes sure that links always works, like for example if pretty print for collections is enabled
    }
}