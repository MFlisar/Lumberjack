package com.michaelflisar.lumberjack.data

import java.util.regex.Pattern


class StackData(
    val className: String,
    val simpleFileName: String,
    val methodName: String,
    val line: Int
) {

    companion object {
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

        fun create(t: Throwable?, callStackIndex: Int): StackData {
            val stackTrace = t?.stackTrace ?: Throwable().stackTrace
            val index = if (t != null) 0 else (callStackIndex + 1)
            if (stackTrace.size <= index) {
                throw IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?"
                )
            }
            val element = stackTrace[index]
            var tag = element.className
            val m = ANONYMOUS_CLASS.matcher(tag)
            if (m.find()) {
                tag = m.replaceAll("")
            }
            return StackData(tag, element.fileName, element.methodName, element.lineNumber)
        }
    }

    val simpleClassName: String = className.substring(className.lastIndexOf('.') + 1)
    val stackTag = "$simpleClassName:$line $methodName"
    val link = "$simpleFileName:$line"
}