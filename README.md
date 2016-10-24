###Lumberjack [![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)

### What does it do?

A simple logger for JackWhartons [Timber](https://github.com/JakeWharton/timber) logging library with following *features*:

* define group of log messages and disable them
* register custom log formatters to *automatically* format classes in your logs
* automatically log first n values of lists/arrays
* trees for logging to console, files (one file per day) and even to log to a notification (with group filter and 
* customise each tree to whatever you want or extend them
* little utility class to log time and laps

**All features are splitted into seperate modules, just include the modules you want to use!**

### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```groovy
dependencies {
    // necessary - base module
    compile 'com.github.MFlisar:Lumberjack:0.1'
    // necessary - optional modules
    compile 'com.github.MFlisar:Lumberjack-filelogger:0.1'
    compile 'com.github.MFlisar:Lumberjack-notification:0.1'
}
```

#####Demo

The best place to start is the demo, it's full with comments that should explain everything.

TODO: correct link + demo activity

[Open demo activity](https://github.com/MFlisar/Lumberjack/blob/master/library/src/main/java/com/michaelflisar/lumberjack/L.java)

