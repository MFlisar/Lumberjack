package com.michaelflisar.lumberjack.implementation.classes

import android.util.Log
import java.util.regex.Pattern

internal data class StackData(
    val stackTrace: List<StackTraceElement>,
    val callStackIndex: Int
) {

    constructor(
        t: Throwable,
        callStackIndex: Int
    ) : this(t.stackTrace.toList(), callStackIndex)

    companion object {
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }

    var element: StackTraceElement? = null
        private set

    val className by lazy {
        getClassName(element)
    }

    val line: Int
        get() = element?.lineNumber ?: -1

    val fileName: String
        get() = element?.fileName ?: ""

    val methodName: String
        get() = element?.methodName ?: ""

    init {
        val index = callStackIndex
        element = getElement(stackTrace, index)
    }

    // ------------------------
    // private helper functions
    // ------------------------

    private fun getElement(stackTrace: List<StackTraceElement>, index: Int): StackTraceElement? {
        if (stackTrace.isEmpty())
            return null
        var i = index
        if (index >= stackTrace.size) {
            i = stackTrace.size - 1
            Log.e("L", "Synthetic stacktrace didn't have enough elements: are you using proguard?")
        }
        return stackTrace[i]
    }

    private fun getClassName(element: StackTraceElement?): String {
        if (element == null)
            return ""
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag
    }
}