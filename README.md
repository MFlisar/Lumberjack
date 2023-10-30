#	About

[![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)
![License](https://img.shields.io/github/license/MFlisar/Lumberjack)

This is a full logging library with a build in way to log to **console**, **file** or any **custom** place as well as optional extensions to send a log file via mail or show it on the device.

### Timber Support

This library fully supports *Jack Whartons* [Timber](https://github.com/JakeWharton/timber) logging library (v4!). And was even based on it until *Lumberjack v6*. Beginning with *v6* I wrote new modules that work without timber which leads to a smaller and more versitile non timber version. I would advice you to use the non timber versions but if you want to you can simply use the timber modules I provide as well - whatever you prefer.

A short summary on why I went this way can be found [here](README-TIMBER.md)

# Table of content

* [Example Outputs](#-example-outputs)
* [Features](#-features)
* [Dependencies](#-dependencies)
* [Setup Gradle](#%EF%B8%8F-setup-gradle)
* [Setup Library](#%EF%B8%8F-setup-library)
* [Usage](#%EF%B8%8F-usage)
* [Demo](#-demo)
* [Modules and Extensions](#-modules-and-extensions)
* [Advanced Usage](#-advanced-usage)
* [Notes](#-notes)

# 📷 Example Outputs

| Console 1 | Console 2 |
|-|-|
| ![Viewer](screenshots/log2.png) | ![Viewer](screenshots/log2.png) |


| File |
|-|
| [Example log file](examples/log.txt) |

| Compose Viewer 1 | Compose Viewer 2 | View Viewer 1 | View Viewer 2 |
|-|-|-|-|
| ![Viewer](screenshots/compose-viewer1.jpg) | ![Viewer](screenshots/compose-viewer2.jpg) | ![Viewer](screenshots/viewer1.jpg) | ![Viewer](screenshots/viewer2.jpg) |

# ✅ Features

* logs will be created with **class name**, **function name** abd **line number** of the calling place automatically
* logs are evaluated lazily, this means, if the content of a log is not needed, it won't be evaluated
* loggers can be *enabled*/*disabled*  and do support filtering logs
* supports arbitrary loggers and provides data like **class name**, **function name**, **line number**
* can be used with a very small custom logging implementation or timber, whatever you prefer
* has extensions for
  * sending a log file via mail (no internet permissions - this is done by appending the log file to an `Intent` and let the user choose an email client)
  * a log file viewer (view or compose based)
  * a notification extension which allows you to show a notification which can show a non crashing but unexpected error and allows the user to click it and send a log file if desired

**All features are splitted into separate modules, just include the modules you want to use!**

# 🔗 Dependencies

| Modules | Dependency | Version |
|:-|:-|-:|
| `core` | - |  |
||| <tr><th colspan="3">Lumberjack Loggers</th></tr> |
|||
| `implementation-lumberjack` | - |  |
| `logger-console` | - |  |
| `logger-file` | - |  |
||| <tr><th colspan="3">Timber Loggers</th></tr> |
|||
| `implementation-timber` | [Timber](https://github.com/JakeWharton/timber) | `4.7.1` |
| `logger-timber-console` | [Timber](https://github.com/JakeWharton/timber) | `4.7.1` |
| `logger-timber-file` | [Timber](https://github.com/JakeWharton/timber) | `4.7.1` |
| | [slf4j](https://www.slf4j.org/) | `2.0.7` |
| | [logback-android](https://github.com/tony19/logback-android) | `3.0.0` |
||| <tr><th colspan="3">Common Extensions</th></tr> |
|||
| `extension-feedback` | [FeedbackManager](https://github.com/MFlisar/FeedbackManager) | `2.0.3` |
| `extension-notification` | [FeedbackManager](https://github.com/MFlisar/FeedbackManager) | `2.0.3` |
| `extension-viewer` | [FastScroller](https://github.com/quiph/RecyclerView-FastScroller) | `1.0.0` |
| `extension-viewer` | - | `2.0.3` |
| `extension-composeviewer` | - | `2.0.3` |

Following dependency only applies to the **extension-composeviewer** module:

| Dependency | Version | Infos |
|:-|-:|:-:|
| [Compose BOM](https://developer.android.com/jetpack/compose/bom/bom) | `2023.10.01` | [Mapping](https://developer.android.com/jetpack/compose/bom/bom-mapping) |
| Material3 | `1.1.2` | |

# 🛠️ Setup Gradle

This library is distributed via [JitPack.io](https://jitpack.io/).

### 1) Add jitpack to your project's `build.gradle`:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

### 2) Add dependencies to your module's `build.gradle`:

```gradle
dependencies {

  val lumberjack = "<LATEST-VERSION>"

  // 1) core module
  implementation("com.github.MFlisar.Lumberjack:core:$lumberjack")
  
  // 2) select a implementation (whichever one you prefer - my suggestion is to don't use timber if you don't need to
  //    as my own implementation is more compact
  // + select the desired loggers (or trees for timber) you want to use
  
  // 2.1) lumberjack versions
  implementation("com.github.MFlisar.Lumberjack:implementation-lumberjack:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:logger-console:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:logger-file:$lumberjack")
  
  // 2.2) timber versions
  implementation("com.github.MFlisar.Lumberjack:implementation-timber:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:logger-timber-console:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:logger-timber-file:$lumberjack")
  
  // 3) select optional extensions if needed
  implementation("com.github.MFlisar.Lumberjack:extension-feedback:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:extension-notification:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:extension-viewer:$lumberjack")
  implementation("com.github.MFlisar.Lumberjack:extension-composeviewer:$lumberjack")
}
```

# 🛠️ Setup Library

You should initialise `Lumberjack` once only - the best place is your apps `application`.

```kotlin
class App : Application() {

    override fun onCreate() {
      
      // ------------------------
      // Variant 1: the lumberjack version
      // ------------------------

      // 1) install the implemantion
      L.init(LumberjackLogger)
      
      // 2) install loggers
      L.plant(ConsoleLogger())
      val setup = FileLoggerSetup.Daily(this)
      L.plant(FileLogger(setup))

      // ------------------------
      // Variant 2: the timber version
      // ------------------------

      // 1) install the implemantion
      L.init(TimberLogger)

      // 2) install loggers (trees) 
      Timber.plant(ConsoleTree())
      val setup = FileLoggingSetup.DateFiles(this  )
      Timber.plant(FileLoggingTree(setup))
    }

}
```

That's it - now you can use `Lumberjack` everywhere in your app.

# ⌨️ Usage

```kotlin

// whereever you want use one of L.* functions for logging
// all the functions are implemented as inline functions with lambdas, this means,
// everything inside the lambda is only executed if the log is really executed

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

// -----------------------
// Settings for Lumberjack
// -----------------------

// if desired you can enable/disable all logs completely - e.g. in release like following
L.enable(BuildConfig.DEBUG)

// -----------------------
// Filtering
// -----------------------

// for the lumberjack implementation you can provide a custom filter for each logging implementation
// => you get ALL data of the log to decide if you want to filter it out (classname, filename, line, log message, level, exception, ...)
// => simple provide a `LumberjackFilter` instance when instantiating the loggers
// definition of the interface looks like following:
// typealias LumberjackFilter = (level: Level, tag: String?, time: Long, fileName: String, className: String, methodName: String, line: Int, msg: String?, throwable: Throwable?) -> Boolean
val filter = object : LumberjackFilter {
    override fun invoke( level: Level, tag: String?, time: Long,fileName: String,className: String,methodName: String,line: Int,msg: String?,throwable: Throwable?): Boolean {
        // decide if you want to log this message...
        return true
    }
}
val consoleLogger = ConsoleLogger(filter = filter)
val fileLogger = FileLogger(filter = filter)

// for the timber implementation you can't filter such granualary, just by tag and package name
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

# 🧬 Demo

A full [demo](demo/src/main/java/com/michaelflisar/lumberjack/demo) is included inside the *demo module*, it shows nearly every usage with working examples.

# 🔷 Modules and Extensions

* you must decide yourself if you want to use the timber modules or the non timber modules. MY suggestion is to prefer the non timber modules as those will save some space and will allow you to even log in a more flexible way.
* despite that, all extensions work with any implementation (timer or non timber one)

### 1) `Extension Feedback`

This small extension simply allows you to send a log file via mail (no internet connection required). This will be done by sharing the file as email `Intent`.

```kotlin
 L.sendFeedback(context, <file-logging-setup>.getLatestLogFiles(), receiverMailAddress)
```

### 2) `Extension Notification`

This small extension provides you with with 2 functions to create notifications (for app testers for example) that can be clicked and then will allow the user to send the log file to you via the `extension-feedback`.

```kotlin
// show a crash notifcation - on notification click the user can send a feedback mail including the log file
L.showCrashNotification(context, logFile /* may be null */, "some.mail@gmail.com", R.mipmap.ic_launcher, "NotificationChannelID", 1234 /* notification id */)

// show a notification to allow the user the report some interesting internal proplems
L.showCrashNotification(context, fileLoggingSetup, "some.mail@gmail.com", R.mipmap.ic_launcher, "NotificationChannelID", 1234 /* notification id */)

// show an information notification to the user (or for dev purposes)
L.showInfoNotification(context, "NotificationChannelID", 1234 /* notification id */, "Notification Title", "Notification Text", R.mipmap.ic_launcher)

// as above, but on notification click it will open the log viewer showing the provided log file
L.showInfoNotification(context, logFile, "NotificationChannelID", 1234 /* notification id */, "Notification Title", "Notification Text", R.mipmap.ic_launcher)
```

### 3) `Extension ComposeViewer`

If you use compose in your app you should use this viewer - it allows you to show log files directly inside your app.

```kotlin
val showLogViewer = rememberSaveable {
    mutableStateOf(false)
}
LumberjackDialog(
    visible = showLogViewer,
    title = "Logs",
    setup = <a file logging setup>,
    mail = "some.mail@gmail.com"
)
```

![Viewer](screenshots/compose-viewer1.jpg)
![Viewer](screenshots/compose-viewer2.jpg)

### 4) `Extension Viewer`

If you do not use compose, here's a view based alternative to show log files inside your app.

```kotlin
// show the log viewer activity (mail address is optional, if it's null, the send mail entry will be removed from the viewers menu)
L.showLog(context, fileLoggingSetup, "some.mail@gmail.com")
```

![Viewer](screenshots/viewer1.jpg)
![Viewer](screenshots/viewer2.jpg)

# 📗 Advanced Usage

### Custom Loggers

Either use the timber version and plug in your custom loggers into timber (check out timber for that please) or simple plug in a custom logger into lumberjack directly if you do not use the timber solution like following.

All you need to do is implementing a single function and then add your logger to `Lumberjack` (following example is the current `ConsoleLogger` implementation).

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

# 📄 Notes

### File Loggers

There's something to say about file loggers. The `timber` version uses `slf4j` + `logback-android` which adds quite some overhead to your app. But those libraries are well tested and solid.

Beginning with v6 I decided to also provide non timber versions of my library and the file logger for this one does not have any dependencies - it simply logs in a background thread with the help of coroutines. This makes this alternative very tiny.
