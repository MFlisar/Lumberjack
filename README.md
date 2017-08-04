### Lumberjack [![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)

### What does it do?

![Demo](https://github.com/MFlisar/Lumberjack/blob/master/files/demo.gif?raw=true)

A simple logger for JackWhartons [Timber](https://github.com/JakeWharton/timber) logging library with following *features*:

* defining group of log messages and filter them, even filter them on a per tree base
* register custom log formatters to *automatically* format classes in your logs
* automatically log first n values of lists/arrays (this *automatically* respects custom log formatters for classes in the collections!)
* trees for:
  * logging to console (with the ability to add clickable links to the calling line in the calling class)
  * files (one file per day, select between numbered log file names or date based log file names)
  * notification (with filter functionality per group, next/prev log buttons and more...)
  * overlay logging (expandable/collapseable overlay, show errors only, pausing)
* customise each tree to whatever you want or extend them
* little utility class to log time and laps

**All features are splitted into seperate modules, just include the modules you want to use!**

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
	compile 'com.github.MFlisar.Lumberjack:lumberjack-library:2.0.2'
    // modules (OPTIONAL)
    compile 'com.github.MFlisar.Lumberjack:lumberjack-filelogger:2.0.2'
    compile 'com.github.MFlisar.Lumberjack:lumberjack-notification:2.0.2'
	debugCompile 'com.github.MFlisar.Lumberjack:lumberjack-overlay:2.0.2'
	releaseCompile 'com.github.MFlisar.Lumberjack:lumberjack-overlay-noop:2.0.2'
    
    // ALTERNATIVELY you can add ALL modules at once like following
    // compile 'com.github.MFlisar:Lumberjack:2.0.2'
}
```

### Example - LOGGING

The logger is simply used like following:

```groovy
// this simply logs a message
L.d("Simpe log");
// arrays/lists can simple be logged directly, the formatter will take care of it
L.d("Simple array log: %s", new ArrayList<>(Arrays.asList("array value 1", "array value 2")));
// group your logs, the formatter takes care of printing the groups and you can filter out groups if you want to
L.withGroup("Group").d("Simple log in group");
// a little helper builder to build object - value pairs and print them to the log
L.d(L.labeledValueBuilder()
    .addPair("String", "Value")
    .addPair("Integer", 999)
    .addPair("Long", 5L));
```


### Example - OUTPUT

This is what the demo setup will print out (to your file, to the notification, to console, to the overlay). It has pretty printing enabled and prints first 5 values of an array automatically. It as well prints caller class and group combined as tag. The demo shows that

* arrays are printed automatically
* custom classes are automatically formatted with the custom formatter you have registered, no matter if the classes are printed inside an array or as a simple value
* caller class, tags and groups are printed

*Example log*

```
[<TEST-GROUP 1> L:65 initLumberjack]: initLumberjack fertig (L.java:65)
[<TEST-GROUP 2> L:66 initLumberjack]: LogFiles: [Type=String, Size=1] (L.java:66)
[
	/data/user/0/com.michaelflisar.lumberjack.demo/files/log_20170301.log
]
[MainActivity:30 onCreate]: Main activity created (MainActivity.java:30)
[MainActivity:31 onCreate]: Test message 1: This is the first simple test log (MainActivity.java:31)
[MainActivity:32 onCreate]: Test error: Test error log (MainActivity.java:32)
java.lang.Throwable: ERROR
    at com.michaelflisar.lumberjack.demo.MainActivity.onCreate(MainActivity.java:32)
    at android.app.Activity.performCreate(Activity.java:6876)
    at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1135)
    at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3207)
    at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3350)
    at android.app.ActivityThread.access$1100(ActivityThread.java:222)
    at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1795)
    at android.os.Handler.dispatchMessage(Handler.java:102)
    at android.os.Looper.loop(Looper.java:158)
    at android.app.ActivityThread.main(ActivityThread.java:7229)
    at java.lang.reflect.Method.invoke(Native Method)
    at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1230)
    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1120)
    at de.robv.android.xposed.XposedBridge.main(XposedBridge.java:102)
[MainActivity:38 onCreate]: Test array log: [Type=String, Size=2] (MainActivity.java:38)
[
	array value 1
	array value 2
]
[<TEST-GROUP 1> MainActivity:41 onCreate]: Test message in test group (MainActivity.java:41)
[<TEST-GROUP 1> MainActivity:42 onCreate]: Test message in test group, value=999 (MainActivity.java:42)
[MainActivity:50 onCreate]: Test custom object log: TestClass: [TestLogData says: My x value is 99] (MainActivity.java:50)
[MainActivity:51 onCreate]: Test custom object array log: [Type=TestClass, Size=3] (MainActivity.java:51)
[
	TestLogData says: My x value is 1
	TestLogData says: My x value is 2
	TestLogData says: My x value is 10
]
[MainActivity:54 onCreate]: String=Value, Integer=999, Long=5 (MainActivity.java:54)
```

### Usage

The best place to start is the [WIKI](https://github.com/MFlisar/Lumberjack/wiki) and the [Demo Activity](https://github.com/MFlisar/Lumberjack/blob/master/demo/app/src/main/java/com/michaelflisar/lumberjack/demo/MainActivity.java).

### Credits

The overlay logger is based on Hannes Dorfmann's DebugOverlay: https://github.com/sockeqwe/debugoverlay
