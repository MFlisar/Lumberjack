### Lumberjack [![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)

### What does it do?

![Demo](https://github.com/MFlisar/Lumberjack/blob/master/files/demo.gif?raw=true)

A simple logger for JackWhartons [Timber](https://github.com/JakeWharton/timber) logging library with following *features*:

* trees for:
  * logging to console (with the ability to add clickable links to the calling line in the calling class)
  * files (one file per day, select between numbered log file names or date based log file names)
* customise each tree to whatever you want or extend them or add your own tree
* little utility class to log time and laps
* by default, this library will create tags if no custom tag is provided like "[CLASSNAME:LINE] FUNCTION" e.g. `[MainActivity:32 onCreate]: Some log`

**All features are splitted into separate modules, just include the modules you want to use!**

### Gradle (via [JitPack.io](https://jitpack.io/))

1) add jitpack to your project's `build.gradle`:

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```

2) add the compile statement to your module's `build.gradle`:

```groovy
dependencies {
    // base module (NECESSARY)
	compile 'com.github.MFlisar.Lumberjack:lumberjack-library:3.0.2'
    // modules (OPTIONAL)
    compile 'com.github.MFlisar.Lumberjack:lumberjack-filelogger:3.0.2'
    
    // ALTERNATIVELY you can add ALL modules at once like following
    // compile 'com.github.MFlisar:Lumberjack:3.0.2'
}
```

### Example - LOGGING

The logger is simply used like following:

```kotlin
// this simply logs a message
L.d { "Simpe log" }
// simply log with custom tag
L.tag("CUSTOM-TAG").d { "Some message with a tag" }
// Log and only run log code based on a function / boolean flag
L.logIf { true }.d { "Is logged, as flag is true" }
L.logIf { someFunction() }.d { "Is logged and only executed if someFunction returns true" }
```

### Example - OUTPUT

This is what the demo setup will print out (to your file, to the notification, to console, to the overlay). It has pretty printing enabled and prints first 5 values of an array automatically. It as well prints caller class and group combined as tag. The demo shows that

* arrays are printed automatically
* custom classes are automatically formatted with the custom formatter you have registered, no matter if the classes are printed inside an array or as a simple value
* caller class, tags and groups are printed

*Example log*

```
[MainActivity:26 onCreate]: 1 - Main activity created (MainActivity.kt:26)
[MainActivity:27 onCreate]: 2 - Test message - count: 0 (MainActivity.kt:27)
[MainActivity:28 onCreate]: 3 - Test error (MainActivity.kt:28)
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
[<CUSTOM-TAG> MainActivity:29 onCreate]: 4 - Test message with custom tag (MainActivity.kt:29)
[MainActivity:32 onCreate]: 5 - Test log based on boolen flag  (MainActivity.kt:32)	
[MainActivity$onCreate:50 run]: 8 - Timer data: Started: true | Laps: 1 | Total = 1501ms | Running: false (MainActivity.kt:50)
```

### Usage

Once in your app do following:

```kotlin
// simply console logger
L.plant(ConsoleTree())
// a file logger
val fileLoggingSetup = FileLoggingSetup(context) // default setup keeps log files for 7 days and creates a new file each day
L.plant(FileLoggingTree(fileLoggingSetup))
```

### Credits

The overlay logger is based on Hannes Dorfmann's DebugOverlay: https://github.com/sockeqwe/debugoverlay

The idea for kotlin inline functions is based on https://github.com/ajalt/timberkt