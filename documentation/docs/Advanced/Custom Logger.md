---
icon: material/professional-hexagon
---

Either use the timber version and plug in your custom loggers into timber (*check out timber for that please*) or simply plug in a custom logger into lumberjack directly if you do not use the timber solution like following - all you need to do is implement a single function and then add your logger to `Lumberjack` (following example is the current `ConsoleLogger` implementation).

```kotlin
class ConsoleLogger(
    override var enabled: Boolean = true,
    override val filter: LumberjackFilter = DefaultLumberjackFilter
) : ILumberjackLogger {

    override fun log(
        level: Level,
        tag: String?,
        time: Long,
        fileName: String,
        className: String,
        methodName: String,
        line: Int,
        msg: String?,
        throwable: Throwable?
    ) {
        val link = "(${fileName}:${line})"
        val log = listOfNotNull(
            msg,
            link.takeIf { throwable == null },
            throwable?.stackTraceToString()?.let { "\n$it" }
        ).joinToString(" ")
        Log.println(level.priority, tag, log)
    }

}
```

That's all. You can do the logging asynchronous as well if you want - just do whatever you want inside your logger implementation.