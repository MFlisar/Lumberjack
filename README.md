## About

[![Release](https://jitpack.io/v/MFlisar/Lumberjack.svg)](https://jitpack.io/#MFlisar/Lumberjack)
![License](https://img.shields.io/github/license/MFlisar/Lumberjack)

This is a **full logging library** with a build in way to log to **console**, **file** or any **custom** place as well as optional extensions to send a log file via mail or show it on the device.

This is KMP (kotlin multiplatform) library and the provided modules do support following platforms:

**Supported Platforms**

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

## :camera: Screenshots

| ![Demo](screenshots/log1.png "Demo") | ![Demo](screenshots/log2.png "Demo") | ![Demo](screenshots/compose-viewer1.jpg "Demo") | ![Demo](screenshots/compose-viewer1.jpg "Demo") |
| :-: | :-: | :-: | :-: |

## :book: Documentation

The readme for this library with **code samples**, **screenshots** and more can be found on following *github page*:

[![Static Badge](https://img.shields.io/badge/Open%20Documentation-lightgreen?style=for-the-badge&logo=github&logoColor=black)](https://mflisar.github.io/github-docs/pages/libraries/lumberjack/)

Additionally there is also a full working [demo app](demo) inside the *demo module*.
