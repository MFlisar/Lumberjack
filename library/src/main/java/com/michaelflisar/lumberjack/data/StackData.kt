package com.michaelflisar.lumberjack.data

import android.annotation.SuppressLint
import android.util.Log
import com.michaelflisar.lumberjack.L
import java.util.regex.Pattern

class StackData(
    stackTrace: Array<StackTraceElement>,
    callStackIndex: Int
) {

    companion object {
        private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")
    }

    var element: StackTraceElement? = null
        private set
    var element2: StackTraceElement? = null
        private set

    val className by lazy {
        getClassName(element)
    }

    val className2 by lazy {
        getClassName(element2)
    }

    init {
        val index = callStackIndex
        element = getElement(stackTrace, index)
        if (L.advancedLambdaLogging) {
            // functions can not start with numbers, so of the class name ends with a number, this must be a lambda which is handled as anonymous function
            val split = element?.className?.split("$")
            if (split?.lastOrNull()?.toIntOrNull() != null) {
                // example from demo:
                // com.michaelflisar.lumberjack.demo.MainActivity$onCreate$func$1
                // => so we need to go up +2, because we want to get the line in onCreate where the caller executes the lambda
                element2 = element
                element = getElement(stackTrace, index + 2)
            }
        }
    }

    // ------------------------
    // functions
    // ------------------------

    fun getLink(): String {
        var link = "(${element?.fileName}:${element?.lineNumber})"
        // AndroidStudio does not support 2 clickable links...
        element2?.let {
            link += " | (${it.fileName}:${it.lineNumber})"
        }
        return link
    }

    fun getCallingPackageName(): String {
        return className
    }

    // ------------------------
    // private helper functions
    // ------------------------

    @SuppressLint("LogNotTimber")
    private fun getElement(stackTrace: Array<StackTraceElement>, index: Int): StackTraceElement? {
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