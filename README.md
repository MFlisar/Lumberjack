[![Maven](https://img.shields.io/maven-central/v/io.github.mflisar.lumberjack/core?style=for-the-badge&color=blue)](https://central.sonatype.com/namespace/io.github.mflisar.lumberjack)
[![API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/github/languages/top/mflisar/kotpreferences.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&label=Kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/github/license/MFlisar/Lumberjack?style=for-the-badge)](LICENSE)

<h1 align="center">Lumberjack</h1>

A <b>lazy logging library</b>.

## :heavy_check_mark: Features

This is a **full logging library** with a build in way to log to **console**, **file** or any **custom** place as well as optional extensions to send a log file via mail or show it on the device.

**All features are splitted into separate modules, just include the modules you want to use!**

## :camera: Screenshots

| ![Demo](screenshots/log1.png "Demo") | ![Demo](screenshots/log2.png "Demo") | ![Demo](screenshots/compose-viewer1.jpg "Demo") | ![Demo](screenshots/compose-viewer1.jpg "Demo") |
| :-: | :-: | :-: | :-: |

## :link: Dependencies

The timber version depends on [`Timber 4.7.1`](https://github.com/JakeWharton/timber), [`SLF4J 2.0.7`](https://www.slf4j.org/) and [`logback-android`](https://github.com/tony19/logback-android), the lumberjack version does not depend on any external library.

## :elephant: Gradle

This library is distributed via [maven central](https://central.sonatype.com/).

*build.gradle.kts*

```kts
val lumberjack = "<LATEST-VERSION>"

// core
implementation("io.github.mflisar.lumberjack:core:$lumberjack")

// if you want to use the lumberjack logger
implementation("io.github.mflisar.lumberjack:implementation-lumberjack:$lumberjack")
implementation("io.github.mflisar.lumberjack:logger-console:$lumberjack")
implementation("io.github.mflisar.lumberjack:logger-file:$lumberjack")

// if you want to use the timber logger
// implementation("io.github.mflisar.lumberjack:implementation-timber:$lumberjack")
// implementation("io.github.mflisar.lumberjack:timber-logger-console:$lumberjack")
// implementation("io.github.mflisar.lumberjack:timber-logger-file:$lumberjack")

// Extensions that work with any implementation
implementation("io.github.mflisar.lumberjack:extension-feedback:$lumberjack")
implementation("io.github.mflisar.lumberjack:extension-notification:$lumberjack")
implementation("io.github.mflisar.lumberjack:extension-viewer:$lumberjack")
implementation("io.github.mflisar.lumberjack:extension-composeviewer:$lumberjack")
```

## :zap: Modules

| Module                      | Info     | Description                                                                                                                  |
|-----------------------------|----------|------------------------------------------------------------------------------------------------------------------------------|
| `core`                      |          | the core module - must always be included                                                                                    |
| `implementation-lumberjack` |          | the main implementation of the logger - either this or the timber implementation must always be included                     |
| `implementation-timber`     |          | the main implementation based on timber of the logger - either this or the lumberjack implementation must always be included |
| `logger-console`            | optional | a console logger for the lumberjack implementation                                                                           |
| `logger-file`               | optional | a console logger for the lumberjack implementation                                                                           |
| `timber-logger-console`     | optional | a console logger for the timber implementation                                                                               |
| `timber-logger-file`        | optional | a console logger for the timber implementation                                                                               |
| `extension-feedback`        | optional | an extension for easy email feedbacks                                                                                        |
| `extension-notification`    | optional | an extension for exception notification with easy email feedbacks on click                                                   |
| `extension-viewer`          | optional | a log viewer based on XML                                                                                                    |
| `extension-composeviewer`   | optional | a compsoe log viewer                                                                                                         |

## </> Basic Usage

<details open>
<summary><b>1.</b> Define a <code>SettingsModel</code></summary>

CONTENT

</details>

## :computer: Supported Platforms

**Supported Platforms**

This is a **KMP (kotlin multiplatform)** library and the provided modules do support following platforms:

*iOS*

iOS would be supported theoretically, but currently I don't know iOS and don't own an apple device - the problem is only that I can't replace `ThreadLocal` and `StackTraceElement` in a non jvm environment... If you know how this can be done and want to contribute, that would be much appreciated.

*Core*

| Modules        | Android | iOS | jvm | Information |
|:---------------|---------|-----|-----|-------------|
| core           | √       | √   | √   |             |

*Lumberjack Modules*

| Modules                   | Android | iOS | jvm | Information |
|:--------------------------|---------|-----|-----|-------------|
| implementation-lumberjack | √       |     | √   | (1)         |
| logger-console            | √       |     | √   | (1)         |
| logger-file               | √       | √   | √   |             |

* (1) iOS is missing support because I don't know how to handle `ThreadLocal` and `StackTraceElement` inside iOS - a pull request would be much appreciated!
* (2) iOS is missing a simple console logging function - a pull request would be much appreciated! 

*Extensions*

| Modules                 | Android | iOS | jvm | Information |
|:------------------------|---------|-----|-----|-------------|
| extension-feedback      | √       |     |     | (2)         |
| extension-notification  | √       |     |     | (2)         |
| extension-viewer        | √       |     |     | (3)         |
| extension-composeviewer | √       | √   |     | (4)         |

* (2) notification and feedback module are android specific modules and therefor only support android
* (3) the viewer module is and older view based module that just supports android based on its nature
* (4) would support iOS if the iOS logger-file module is implemented

*Timber Modules*

| Modules               | Android | iOS | jvm | Information |
|:----------------------|---------|-----|-----|-------------|
| implementation-timber | √       |     |     | (5)         |
| logger-timber-console | √       |     |     | (5)         |
| logger-timber-file    | √       |     |     | (5)         |

* (5) timber is only supported on android and therefor those modules are android only modules as well

## :tada: Demo

A full [demo](demo) is included inside the demo module, it shows nearly every usage with working examples.



## :information_source: More Informations

* [Feedback Module](readmes/module-encryption.md)
* [Notification Module](readmes/module-datastore.md)
* [ComposeViewer Module](readmes/module-key-value.md)
* [Viewer Module](readmes/module-key-value.md)
