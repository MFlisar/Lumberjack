package com.michaelflisar.lumberjack.data

import java.util.regex.Pattern


class StackData(
        val className: String,
        private val simpleFileName: String,
        private val methodName: String,
        private val line: Int
) {

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
}