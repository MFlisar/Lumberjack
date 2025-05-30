package com.michaelflisar.lumberjack.implementation.classes

internal data class StackData(
    val throwable: Throwable,
    val callStackIndex: Int,
) {
    var fileName = ""
        private set
    var className = ""
        private set
    var methodName = ""
        private set
    var line = -1
        private set

    init {
        /*
         * Examples:
         * callStackIndex always 1
         *
         * java.lang.Throwable
            at com.michaelflisar.lumberjack.core.AbstractLogger.d(AbstractLogger.kt:28)
            at com.michaelflisar.lumberjack.demo.MainKt.main(Main.kt:57)
            at com.michaelflisar.lumberjack.demo.MainKt.main(Main.kt)
         * ---------------------------------------------------------------------
         * java.lang.Throwable
            at com.michaelflisar.lumberjack.core.AbstractLogger.d(AbstractLogger.kt:28)
            at com.michaelflisar.lumberjack.demo.Test.testLogInClass(Main.kt:158)
            at com.michaelflisar.lumberjack.demo.MainKt.main(Main.kt:59)
            at com.michaelflisar.lumberjack.demo.MainKt.main(Main.kt)
         * ---------------------------------------------------------------------
         * java.lang.Throwable
            at com.michaelflisar.lumberjack.core.AbstractLogger.d(AbstractLogger.kt:28)
            at com.michaelflisar.lumberjack.demo.MainKt$main$1$2$1$2$1$1.invokeSuspend(Main.kt:89)
            ...
         * ---------------------------------------------------------------------
         * java.lang.ArithmeticException: / by zero
            at com.michaelflisar.lumberjack.demo.MainKt$main$2$1$1.invokeSuspend(Main.kt:77)
            ...
         */
        val stackTrace = throwable.stackTraceToString()
        val stackLines = stackTrace.split("\n").map { it.trim() }
        val relevantLine = stackLines.getOrNull(1 + callStackIndex)

        if (relevantLine != null) {

            // function call info:
            // - com.michaelflisar.lumberjack.demo.MainKt.main
            // - com.michaelflisar.lumberjack.demo.Test.testLogInClass
            // - com.michaelflisar.lumberjack.demo.MainKt$main$1$2$1$2$1$1.invokeSuspend
            // - com.michaelflisar.lumberjack.demo.MainKt$main$2$1$1.invokeSuspend
            val infoWithoutCallSide = relevantLine.substringBefore("(").substringAfter("at ")

            // Call Side Info:
            // - Main.kt:158
            val callSideInfo = relevantLine.substringAfter("(").substringBefore(")")



            // 1) file name + line:
            // - Main.kt:57
            fileName = callSideInfo.substringBefore(":")
            line = callSideInfo.substringAfter(":").toIntOrNull() ?: -1

            // 2) class name + method name:
            // ...

            val path: String
            if (relevantLine.contains("$")) {
                path = infoWithoutCallSide.substringBefore("$")
                val afterDollar = relevantLine.substringAfter("$")
                methodName = afterDollar.substringBefore("$")
            } else {
                path = infoWithoutCallSide
                methodName = infoWithoutCallSide.substringAfterLast(".")
            }

            //val fileNameInPath = path.split(".").dropLast(1).lastOrNull() ?: ""
            //val fileNameInComments = callSideInfo.substringBefore(":")

            className = path

            //println("StackData: fileName='$fileName', className='$className', methodName='$methodName', line=$line")
            //println("    - fileNameInPath = $fileNameInPath")
            //println("    - fileNameInComments = $fileNameInComments")
            //println("    - path = $path")
        }
    }
}

// JVM Version with StackTraceElement...
/*
internal data class StackData2(
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
            platformPrintln("", Level.ERROR, "StackData", error)
        }
        return stackTrace[i]
    }

    private fun getClassName(element: StackTraceElement?): String {
        if (element == null)
            return ""
        return element.className.replace(ANONYMOUS_CLASS, "")
    }
}*/