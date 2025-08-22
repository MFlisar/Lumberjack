---
icon: material/keyboard
---

#### 1. Setup library

=== "Lumberjack"

    ```kotlin
    class App : Application() {
    
        override fun onCreate() {
    
             // 1) install the implemantion
             L.init(LumberjackLogger)
             
             // 2) install loggers
             L.plant(ConsoleLogger())
             val setup = FileLoggerSetup.Daily(this)
             L.plant(FileLogger(setup))
        }
    
    }
    ```

=== "Timber"

    ```kotlin
    class App : Application() {
    
        override fun onCreate() {
          
             // 1) install the implemantion
             L.init(TimberLogger)
             
             // 2) install loggers (trees) 
             Timber.plant(ConsoleTree())
             val setup = FileLoggingSetup.DateFiles.create(this  )
             Timber.plant(FileLoggingTree(setup))
        }
    
    }
    ```

#### 2. Usage

```kotlin
// wherever you want use one of L.* functions for logging
// all the functions are implemented as inline functions with lambdas - this means,
// everything inside the lambda is only executed if the log is really ussed

L.d { "a debug log" }
L.e { "a error log" }
L.e(e)
L.e(e) { "an exception log with an additonal message" }
L.v { "TEST-LOG - Verbose log..." }
L.d { "TEST-LOG - Debug log..." }
L.i { "TEST-LOG - Info log..." }
L.w { "TEST-LOG - Warn log..." }
L.e { "TEST-LOG - Error log..." }
L.wtf { "TEST-LOG - WTF log..." }

// optional tags work like following
L.tag("LEVEL").d { "Tagged log message..." }

// you can log something optionally like following
L.logIf { false }?.d { "This will never be logged because logIf evaluates to false..." }

// manual log levels
L.log(Level.DEBUG) { "Debug level log via L.log instead of L.d" }
```

#### 3. Filtering Logs

=== "Lumberjack"

    ```kotlin
    // typealias LumberjackFilter = (level: Level, tag: String?, time: Long, fileName: String, className: String, methodName: String, line: Int, msg: String?, throwable: Throwable?) -> Boolean
    val filter = object : LumberjackFilter {
        override fun invoke(
            level: Level,
            tag: String?,
            time: Long,
            fileName: String,
            className: String,
            methodName: String,
            line: Int,
            msg: String?,
            throwable: Throwable?
        ): Boolean {
            // decide if you want to log this message...
            return true
        }
    }
    // the filter can then be attached to any logger implementation
    val consoleLogger = ConsoleLogger(filter = filter)
    val fileLogger = FileLogger(filter = filter)
    ```

=== "Timber"

    !!! tip
    
        The lumberjack implementation allows you more granular filter options as well as a custom filter for each logger implementation!
    
    ```kotlin
    TimberLogger.filter = object: IFilter {
        override fun isTagEnabled(baseTree: BaseTree, tag: String): Boolean {
            // decide if you want to log this tag on this tree...
            return true
        }
        override fun isPackageNameEnabled(packageName: String): Boolean {
            // decide if you want to log if the log comes from a class within the provided package name
            return true
        }
    }
    ```

#### 4. Other settings

```kotlin

val minLogLevel = if (BuildConfig.DEBUG) Level.DEBUG else Level.VERBOSE // use Level.NONE to disable all logs

// if desired you can enable/disable all logs completely 
// e.g. in a release build like following 
// => you probably would want to do this inside the application after the init of Lumberjack
L.enable(minLogLevel) 

// Alternatively every logger does support a minLogLevel flag as well
val consoleLogger = ConsoleLogger(minLogLevel = minLogLevel)
val fileLogger = FileLogger(minLogLevel = minLogLevel, ...)

```