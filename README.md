###Lumberjack [![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)

### What does it do?

A simple logger for JackWhartons [Timber](https://github.com/JakeWharton/timber) logging library with following *features*:

* define group of log messages and disable them
* register custom log formatters to *automatically* format classes in your logs
* automatically log first n values of lists/arrays (this *automatically* respects custom log formatters for classes in the collections!)
* trees for:
  * logging to console (with the ability to add clickable links to the calling line in the calling class)
  * files (one file per day)
  * notification (with filter functionality per group, next/prev log buttons and more...)
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
    // necessary - base module
	compile 'com.github.MFlisar.Lumberjack:lumberjack-library:1.2'
    // necessary - optional modules
    compile 'com.github.MFlisar.Lumberjack:lumberjack-filelogger:1.2'
    compile 'com.github.MFlisar.Lumberjack:lumberjack-notification:1.2'
    
    // ALTERNATIVELY you can add ALL modules at once like following
    // compile 'com.github.MFlisar:Lumberjack:1.2'
}
```

###Example

This is what the demo setup will print out (to your file, to the notification, to console). It has pretty printing enabled and prints first 5 values of an array automatically. It as well prints caller class and group combined as tag. The demo shows that

* arrays are printed automatically
* custom classes are automatically formatter to the custom formatter you have registered, no matter if the classes are printed inside an array or as a simple value
* caller class, tags and groups are printed

*Example log*

		[MainActivity:17 onCreate]: Main activity created
		[MainActivity:18 onCreate]: Test message 1: This is the first simple test log
		[MainActivity:19 onCreate]: Test error: Test error log
		 java.lang.Throwable: ERROR
			 at com.michaelflisar.lumberjack.demo.MainActivity.onCreate(MainActivity.java:19)
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
		[MainActivity:25 onCreate]: Test array log: [Type=String, Size=2] 
		[
			array value 1
			array value 2
		]
		[<TEST-GROUP> MainActivity:28 onCreate]: Test message in test group
		[<TEST-GROUP> MainActivity:29 onCreate]: Test message in test group, value=999
		[MainActivity:38 onCreate]: Test custom object log: TestClass: [TestLogData - x=99]
		[MainActivity:39 onCreate]: Test custom object array log: [Type=TestClass, Size=3] 
		[
			TestLogData - x=1
			TestLogData - x=2
			TestLogData - x=10
		]

###Usage

The best place to start is the [WIKI](https://github.com/MFlisar/Lumberjack/wiki) and the [Demo Activity](https://github.com/MFlisar/Lumberjack/blob/master/demo/app/src/main/java/com/michaelflisar/lumberjack/demo/MainActivity.java).
