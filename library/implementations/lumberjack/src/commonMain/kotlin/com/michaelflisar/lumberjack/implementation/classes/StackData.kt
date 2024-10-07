package com.michaelflisar.lumberjack.implementation.classes

import com.michaelflisar.lumberjack.core.classes.Level
import com.michaelflisar.lumberjack.implementation.Log

internal data class StackData(
    val stackTrace: List<StackTraceElement>,
    val callStackIndex: Int
) {

    constructor(
        t: Throwable,
        callStackIndex: Int
    ) : this(t.stackTrace.toList(), callStackIndex)

    companion object {
        private val ANONYMOUS_CLASS = "(\\$\\d+)+$".toRegex()
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
            val error = "Synthetic stacktrace didn't have enough elements: are you using proguard?"
            println(error)
            Log.println("", Level.ERROR, "StackData", error)
        }
        return stackTrace[i]
    }

    private fun getClassName(element: StackTraceElement?): String {
        if (element == null)
            return ""
        return element.className.replace(ANONYMOUS_CLASS, "")
    }
}