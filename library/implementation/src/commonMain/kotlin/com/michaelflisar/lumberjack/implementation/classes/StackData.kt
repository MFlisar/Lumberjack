package com.michaelflisar.lumberjack.implementation.classes

internal data class StackData(
    val fileName: String,
    val className: String,
    val methodName: String,
    val line: Int
) {
    companion object {

        fun createDefault(throwable: Throwable, callStackIndex: Int): StackData {
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

            var fileName = ""
            var className = ""
            var methodName = ""
            var line = -1

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

            return StackData(
                fileName = fileName,
                className = className,
                methodName = methodName,
                line = line
            )
        }

        /**
         * Example stack:
         * Stack trace START
         *     kotlin.Throwable
         *     at 0   Lumberjack.debug.dylib              0x105a80bf7        kfun:kotlin.Throwable#<init>(){} + 75
         *     at 1   Lumberjack.debug.dylib              0x1051b7e63        kfun:com.michaelflisar.lumberjack.core.AbstractLogger#d(kotlin.Function0<kotlin.String>){} + 471
         *     at 2   Lumberjack.debug.dylib              0x1051fbbaf        kfun:com.michaelflisar.lumberjack.demo.DemoLogging#run(kotlinx.coroutines.CoroutineScope;kotlin.coroutines.CoroutineContext){} + 215
         *     at 3   Lumberjack.debug.dylib              0x1051f694b        kfun:com.michaelflisar.lumberjack.demo.DemoContent$$inlined$cache$1.invoke#internal + 171
         *     at 4   Lumberjack.debug.dylib              0x105b7e21f        kfun:kotlin.Function2#invoke(1:0;1:1){}1:2-trampoline + 115
         *     at 5   Lumberjack.debug.dylib              0x105a8866f        kfun:kotlin.coroutines.intrinsics.createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$4.invokeSuspend#internal + 827
         *     at 6   Lumberjack.debug.dylib              0x105b7dbd3        kfun:kotlin.coroutines.native.internal.BaseContinuationImpl#invokeSuspend(kotlin.Result<kotlin.Any?>){}kotlin.Any?-trampoline + 67
         *     at 7   Lumberjack.debug.dylib              0x105a858d3        kfun:kotlin.coroutines.native.internal.BaseContinuationImpl#resumeWith(kotlin.Result<kotlin.Any?>){} + 675
         *     at 8   Lumberjack.debug.dylib              0x105b7dca3        kfun:kotlin.coroutines.Continuation#resumeWith(kotlin.Result<1:0>){}-trampoline + 99
         *     at 9   Lumberjack.debug.dylib              0x105c5f50f        kfun:kotlinx.coroutines.DispatchedTask#run(){} + 2039
         *     at 10  Lumberjack.debug.dylib              0x105c8becb        kfun:kotlinx.coroutines.Runnable#run(){}-trampoline + 91
         *     at 11  Lumberjack.debug.dylib              0x1061ed1cb        kfun:androidx.compose.ui.platform.FlushCoroutineDispatcher.FlushCoroutineDispatcher$dispatch$1.FlushCoroutineDispatcher$dispatch$1$invoke$2.invoke#internal + 499
         *     at 12  Lumberjack.debug.dylib              0x1061ed29f        kfun:androidx.compose.ui.platform.FlushCoroutineDispatcher.FlushCoroutineDispatcher$dispatch$1.FlushCoroutineDispatcher$dispatch$1$invoke$2.$<bridge-DN>invoke(){}#internal + 71
         *     at 13  Lumberjack.debug.dylib              0x105b7b627        kfun:kotlin.Function0#invoke(){}1:0-trampoline + 99
         *     at 14  Lumberjack.debug.dylib              0x1061ec82f        kfun:androidx.compose.ui.platform.FlushCoroutineDispatcher.performRun#internal + 359
         *     at 15  Lumberjack.debug.dylib              0x1061ecef7        kfun:androidx.compose.ui.platform.FlushCoroutineDispatcher.FlushCoroutineDispatcher$dispatch$1.invoke#internal + 235
         *     at 16  Lumberjack.debug.dylib              0x105b7e21f        kfun:kotlin.Function2#invoke(1:0;1:1){}1:2-trampoline + 115
         *     at 17  Lumberjack.debug.dylib              0x105a8866f        kfun:kotlin.coroutines.intrinsics.createCoroutineUnintercepted$$inlined$createCoroutineFromSuspendFunction$4.invokeSuspend#internal + 827
         *     at 18  Lumberjack.debug.dylib              0x105b7dbd3        kfun:kotlin.coroutines.native.internal.BaseContinuationImpl#invokeSuspend(kotlin.Result<kotlin.Any?>){}kotlin.Any?-trampoline + 67
         *     at 19  Lumberjack.debug.dylib              0x105a858d3        kfun:kotlin.coroutines.native.internal.BaseContinuationImpl#resumeWith(kotlin.Result<kotlin.Any?>){} + 675
         *     at 20  Lumberjack.debug.dylib              0x105b7dca3        kfun:kotlin.coroutines.Continuation#resumeWith(kotlin.Result<1:0>){}-trampoline + 99
         *     at 21  Lumberjack.debug.dylib              0x105c5f50f        kfun:kotlinx.coroutines.DispatchedTask#run(){} + 2039
         *     at 22  Lumberjack.debug.dylib              0x105c8becb        kfun:kotlinx.coroutines.Runnable#run(){}-trampoline + 91
         *     at 23  Lumberjack.debug.dylib              0x105c884cf        kfun:kotlinx.coroutines.DarwinMainDispatcher.DarwinMainDispatcher$dispatch$$inlined$autoreleasepool$1.invoke#internal + 71
         *     at 24  Lumberjack.debug.dylib              0x105c88527        kfun:kotlinx.coroutines.DarwinMainDispatcher.DarwinMainDispatcher$dispatch$$inlined$autoreleasepool$1.$<bridge-DN>invoke(){}#internal + 71
         *     at 25  Lumberjack.debug.dylib              0x105b7b627        kfun:kotlin.Function0#invoke(){}1:0-trampoline + 99
         *     at 26  Lumberjack.debug.dylib              0x105c883bf        _6f72672e6a6574627261696e732e6b6f746c696e783a6b6f746c696e782d636f726f7574696e65732d636f72652f6f70742f6275696c644167656e742f776f726b2f343465633665383530643563363366302f6b6f746c696e782d636f726f7574696e65732d636f72652f6e617469766544617277696e2f7372632f44697370617463686572732e6b74_6_knbridge3 + 139
         *     at 27  libdispatch.dylib                   0x100c2bfb7        _dispatch_call_block_and_release + 23
         *     at 28  libdispatch.dylib                   0x100c459db        _dispatch_client_callout + 11
         *     at 29  libdispatch.dylib                   0x100c3b5f7        _dispatch_main_queue_drain + 1219
         *     at 30  libdispatch.dylib                   0x100c3b123        _dispatch_main_queue_callback_4CF + 39
         *     at 31  CoreFoundation                      0x180455ed7        __CFRUNLOOP_IS_SERVICING_THE_MAIN_DISPATCH_QUEUE__ + 11
         *     at 32  CoreFoundation                      0x1804550af        __CFRunLoopRun + 1883
         *     at 33  CoreFoundation                      0x18044fceb        _CFRunLoopRunSpecificWithOptions + 495
         *     at 34  GraphicsServices                    0x192a669bb        GSEventRunModal + 115
         *     at 35  UIKitCore                           0x186348573        -[UIApplication _run] + 771
         *     at 36  UIKitCore                           0x18634c79b        UIApplicationMain + 123
         *     at 37  SwiftUI                             0x1da58d61f        $s7SwiftUI17KitRendererCommon33_ACC2C5639A7D76F611E170E831FCA491LLys5NeverOyXlXpFAESpySpys4Int8VGSgGXEfU_ + 163
         *     at 38  SwiftUI                             0x1da58d367        $s7SwiftUI6runAppys5NeverOxAA0D0RzlF + 179
         *     at 39  SwiftUI                             0x1da31b42b        $s7SwiftUI3AppPAAE4mainyyFZ + 147
         *     at 40  Lumberjack.debug.dylib              0x1051b637b        $s10Lumberjack6iOSAppV5$mainyyFZ + 39
         *     at 41  Lumberjack.debug.dylib              0x1051b6427        __debug_main_executable_dylib_entry_point + 11
         *     at 42  dyld                                0x1001553cf        0x0 + 4296365007
         *     at 43  ???                                 0x1002f0d53        0x0 + 4298050899
         *
         *     ------------------------
         *     result should be:        StackData(fileName=DemoLogging.kt, className=com.michaelflisar.lumberjack.demo.DemoLogging.run, methodName=run, line=39)
         *     result currently is:     StackData(fileName=AbstractLogger.kt, className=com.michaelflisar.lumberjack.core.AbstractLogger, methodName=d, line=471)
         */

        // kfunPart = com.michaelflisar.lumberjack.demo.DemoLogging#run(kotlinx.coroutines.CoroutineScope;kotlin.coroutines.CoroutineContext)
        // Created StackData: StackData(fileName=DemoLogging.kt, className=, methodName=run, line=215) | callStackIndex: 1
        // funPart = com.michaelflisar.lumberjack.demo.DemoContent$1.DemoContent$1$invoke$2.DemoContent$1$invoke$2$invoke$$inlined$cache$4.invoke#internal + 563
        // Created StackData: StackData(fileName=invoke.kt, className=, methodName=internal, line=563) | callStackIndex: 1
        fun createNative(throwable: Throwable, callStackIndex: Int): StackData {

            val stackTrace = throwable.stackTraceToString()
            val stackLines = stackTrace.split("\n").map { it.trim() }
            // Filtere nur Zeilen mit kfun:
            val kfunLines = stackLines.filter { it.contains("kfun:") }
            val relevantLine = kfunLines.getOrNull(1 + callStackIndex)

            var fileName = ""
            var className = ""
            var methodName = ""
            var line = -1

            if (relevantLine != null) {
                // Beispielzeile:
                // kfun:com.michaelflisar.lumberjack.demo.DemoLogging#run(kotlinx.coroutines.CoroutineScope;kotlin.coroutines.CoroutineContext){} + 215
                // kfun:com.michaelflisar.lumberjack.demo.DemoContent$1.DemoContent$1$invoke$2.DemoContent$1$invoke$2$invoke$$inlined$cache$4.invoke#internal + 563
                val kfunPart = relevantLine.substringAfter("kfun:").substringBefore("{}")
                val offset = relevantLine.substringAfter("+").trim().toIntOrNull() ?: -1

                // line is not correct anyways in native stack traces, so we ignore it for now
                //line = offset

                val hashIndex = kfunPart.indexOf('#')

                if (hashIndex > 0) {

                    // 1) class name
                    className = kfunPart.take(hashIndex)
                    val dollarIndex = kfunPart.indexOf('$')
                    if (dollarIndex > 0) {
                        className = kfunPart.take(dollarIndex)
                    }

                    // 2) method name
                    methodName = kfunPart.substring(hashIndex + 1).substringBefore("(").substringBefore(" ")

                    // 3) fileName => we don't have this info in native stack traces
                    // we use the class name
                    fileName = className
                }
            }

            return StackData(
                fileName = fileName,
                className = className,
                methodName = methodName,
                line = line
            )
        }
    }
}