### Lumberjack [![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)

### What does it do?

A simple logger for JackWhartons [Timber](https://github.com/JakeWharton/timber) logging library with following *features*:

* trees for:
  * logging to console (with the ability to add clickable links to the calling line in the calling class)
  * files (one file per day, select between numbered log file names or date based log file names)
* customise each tree to whatever you want or extend them or add your own tree
* little utility class to log time and laps (the `T` class) 
* by default, this library will create tags if no custom tag is provided like "[CLASSNAME:LINE] FUNCTION" e.g. `[MainActivity:32 onCreate]: Some log`

**All features are splitted into separate modules, just include the modules you want to use!**

### Gradle (via [JitPack.io](https://jitpack.io/))

1) add jitpack to your project's `build.gradle`:

```
repositories {
    maven { url "https://jitpack.io" }
}
```

2) add the compile statement to your module's `build.gradle`:

```
dependencies {
    // base module (NECESSARY)
	implementation 'com.github.MFlisar.Lumberjack:lumberjack-library:<LATEST-VERSION>'
    // modules (OPTIONAL)
    implementation 'com.github.MFlisar.Lumberjack:lumberjack-filelogger:<LATEST-VERSION>'
    
    // ALTERNATIVELY you can add ALL modules at once like following
    // implementation 'com.github.MFlisar:Lumberjack:<LATEST-VERSION>'

    // Wrapper for java => will provide a class `L2` with all the functions from `L` but without the inlining feature because this is not possible in java
    // can be used to use Lumberjack in mixed java and kotlin projects
    // implementation 'com.github.MFlisar.Lumberjack:lumberjack-java-wrapper:<LATEST-VERSION>'
}
```

### Usage

Once in your app do following:

```kotlin
// simply console logger
L.plant(ConsoleTree())
// a file logger (optional)
val fileLoggingSetup = FileLoggingSetup(context) // default setup keeps log files for 7 days and creates a new file each day
L.plant(FileLoggingTree(fileLoggingSetup))
// if desired, disable all logging in release
// L.enabled = Build.DEBUG
```

### Example - LOGGING

The logger is simply used like following:

```kotlin
// this simply logs a message
L.d { "Simpe log" }
// simply log with custom tag
L.tag("CUSTOM-TAG").d { "Some message with a tag" }
// Log and only run log code based on a function / boolean flag
L.logIf { true }?.d { "Is logged, as flag is true" }
L.logIf { someFunction() }?.d { "Is logged and only executed if someFunction returns true" }
```

If used with `logIf` the expression is only executed if `logIf` returns true so it's save to keep all the logging lines in production code.

### Example - OUTPUT

```
[MainActivity:26 onCreate]: Main activity created (MainActivity.kt:26)
[MainActivity:27 onCreate]: Test message - count: 0 (MainActivity.kt:27)
[MainActivity:28 onCreate]: Test error (MainActivity.kt:28)
    java.lang.Throwable: ERROR
        at com.michaelflisar.lumberjack.demo.MainActivity.onCreate(MainActivity.kt:28)
        at android.app.Activity.performCreate(Activity.java:7183)
        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1220)
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2908)
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3030)
        at android.app.ActivityThread.-wrap11(Unknown Source:0)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1696)
        at android.os.Handler.dispatchMessage(Handler.java:105)
        at android.os.Looper.loop(Looper.java:164)
        at android.app.ActivityThread.main(ActivityThread.java:6938)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:327)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1374)
```