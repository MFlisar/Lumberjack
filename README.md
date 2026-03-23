[![Maven Central](https://img.shields.io/maven-central/v/io.github.mflisar.lumberjack/core?style=for-the-badge&color=blue)](https://central.sonatype.com/artifact/io.github.mflisar.lumberjack/core) ![API](https://img.shields.io/badge/api-23%2B-brightgreen.svg?style=for-the-badge) ![Kotlin](https://img.shields.io/github/languages/top/MFlisar/Lumberjack.svg?style=for-the-badge&amp;color=blueviolet) ![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&amp;label=Kotlin)
# Lumberjack
![Platforms](https://img.shields.io/badge/PLATFORMS-black?style=for-the-badge) ![Android](https://img.shields.io/badge/android-3DDC84?style=for-the-badge) ![iOS](https://img.shields.io/badge/ios-A2AAAD?style=for-the-badge) ![Windows](https://img.shields.io/badge/windows-5382A1?style=for-the-badge) ![macOS](https://img.shields.io/badge/macos-B0B0B0?style=for-the-badge) ![WebAssembly](https://img.shields.io/badge/wasm-624DE7?style=for-the-badge)

This library provides following main features:

* simply lazy evalution based logger based on a `L` class for the shortest possible log codes
* easily extendible with custom loggers
* offers console and file logger implementations
* supports log filtering
* providers a viewer for log files

# Table of Contents

- [Screenshots](#camera-screenshots)
- [Supported Platforms](#computer-supported-platforms)
- [Setup](#wrench-setup)
- [Usage](#rocket-usage)
- [Modules](#file_folder-modules)
- [Demo](#sparkles-demo)
- [More](#information_source-more)
- [API](#books-api)
- [Other Libraries](#bulb-other-libraries)

# :camera: Screenshots

![composeviewer](documentation/screenshots/composeviewer)
![viewer](documentation/screenshots/viewer)
![core](documentation/screenshots/core)

# :computer: Supported Platforms

| Module | android | iOS | windows | macOS | wasm |
|---|---|---|---|---|---|
| core | ✅ | ✅ | ✅ | ✅ | ✅ |
| implementation | ✅ | ✅ | ✅ | ✅ | ✅ |
| logger-console | ✅ | ✅ | ✅ | ✅ | ✅ |
| logger-file | ✅ | ✅ | ✅ | ✅ | ❌ |
| extension-composeviewer | ✅ | ✅ | ✅ | ✅ | ✅ |
| extension-feedback | ✅ | ✅ | ❌ | ❌ | ❌ |

# :wrench: Setup

<details open>

<summary><b>Using Version Catalogs</b></summary>

<br>

Define the dependencies inside your **libs.versions.toml** file.

```toml
[versions]

lumberjack = "<LATEST-VERSION>"

[libraries]

core = { module = "io.github.mflisar.lumberjack:core", version.ref = "lumberjack" }
implementation = { module = "io.github.mflisar.lumberjack:implementation", version.ref = "lumberjack" }
logger.console = { module = "io.github.mflisar.lumberjack:logger-console", version.ref = "lumberjack" }
logger.file = { module = "io.github.mflisar.lumberjack:logger-file", version.ref = "lumberjack" }
extension.composeviewer = { module = "io.github.mflisar.lumberjack:extension-composeviewer", version.ref = "lumberjack" }
extension.feedback = { module = "io.github.mflisar.lumberjack:extension-feedback", version.ref = "lumberjack" }
```

And then use the definitions in your projects **build.gradle.kts** file like following:

```java
implementation(libs.core)
implementation(libs.implementation)
implementation(libs.logger.console)
implementation(libs.logger.file)
implementation(libs.extension.composeviewer)
implementation(libs.extension.feedback)
```

</details>

<details>

<summary><b>Direct Dependency Notation</b></summary>

<br>

Simply add the dependencies inside your **build.gradle.kts** file.

```kotlin
val lumberjack = "<LATEST-VERSION>"

implementation("io.github.mflisar.lumberjack:core:${lumberjack}")
implementation("io.github.mflisar.lumberjack:implementation:${lumberjack}")
implementation("io.github.mflisar.lumberjack:logger-console:${lumberjack}")
implementation("io.github.mflisar.lumberjack:logger-file:${lumberjack}")
implementation("io.github.mflisar.lumberjack:extension-composeviewer:${lumberjack}")
implementation("io.github.mflisar.lumberjack:extension-feedback:${lumberjack}")
```

</details>

# :rocket: Usage

#### 1. Define a `SettingsModel`

```kotlin
// Depending on the platform:
//   - common: DataStoreStorage.create(name = "preferences")
//   - jvm: DataStoreStorage.create(folder = File(System.getProperty("user.dir")), name = "preferences")
//   - android/iOS: DataStoreStorage.create(name = "preferences")
//   - ...
object Preferences : SettingsModel(DataStoreStorage.create(name = "preferences")) {

    // main data types
    val someString by stringPref("value")
    val someBool by boolPref(false)
    val someInt by intPref(123)
    val someLong by intPref(123L)
    val someFloat by intPref(123f)
    val someDouble by intPref(123.0)

    // enum
    val someEnum by enumPref(Enum.Value1)

    // custom
    val someCustomClass1 by anyStringPref(TestClass.CONVERTER, TestClass()) // converts TestClass to a string and saves this string
    val someCustomClass2 by anyIntPref(TestClass.CONVERTER, TestClass())    // converts TestClass to an int and saves this int
    val someCustomClass3 by anyLongPref(TestClass.CONVERTER, TestClass())   // converts TestClass to a long and saves this long

    // sets
    val someStringSet by stringSetPref(setOf("a"))
    val someIntSet by intSetPref(setOf(1))
    val someLongSet by longSetPref(setOf(1L))
    val someFloatSet by floatSetPref(setOf(1f))
    val someDoubleSet by doubleSetPref(setOf(1.0))

    // NULLABLE vs NON NULLABLE
    val nonNullableString by stringPref()
    val nullableString by nullableStringPref()
    val nonNullableInt by intPref()
    val nullableInt by nullableIntPref()
    val nonNullableFloat by floatPref()
    val nullableFloat by nullableFloatPref()
    val nonNullableDouble by doublePref()
    val nullableDouble by nullableDoublePref()
    val nonNullableLong by longPref()
    val nullableLong by nullableLongPref()
    val nonNullableBool by boolPref()
    val nullableBool by nullableBoolPref()

    // custom
    val someCustomClass4 by nullableAnyStringPref(TestClass.CONVERTER, TestClass())
    val someCustomClass5 by nullableAnyIntPref(TestClass.CONVERTER, TestClass())
    val someCustomClass6 by nullableAnyLongPref(TestClass.CONVERTER, TestClass())
}
```

#### 2a) Use the `SettingsModel` in plain kotlin (`flows` + `suspending functions`)

```kotlin
// 1) get a flow
val flow = Preferences.someString.flow

// 2) read/update values by suspend functions
scope.launch(Dispatchers.IO) {
    val value = Preferences.someInt.read()
    Preferences.someInt.update(value + 1)
}
```

#### 2b) Use the `SettingsModel` in views (e.g. with `Lifecycle Scope`)

```kotlin
// 1) simply observe a setting
Preferences.someString.observe(lifecycleScope) {
    L.d { "someString = $it"}
}

// 2) direct read (not recommended if not necessary but may be useful in many cases)
// => simply returns read() in a blocking way)
val name = Preferences.someString.value

// 3) observe a setting once
Preferences.someString.observeOnce(lifecycleScope) {
    L.d { "someString = $it"}
}

// 4) observe ALL settings
Preferences.changes.onEach {
    L.d { "[ALL SETTINGS OBSERVER] Setting '${it.setting.key}' changed its value to ${it.value}" }
}.launchIn(lifecycleScope)

// 5) observe SOME settings
Preferences.changes
    .filter {
        it.setting == Preferences.someString ||
        it.setting == Preferences.someBool
    }.onEach {
        L.d { "[SOME SETTINGS OBSERVER] Setting '${it.setting.key}' changed its value to ${it.value}" }
    }.launchIn(lifecycleScope)

// 6) read multiple settings in a suspending way
lifecycleScope.launch(Dispatchers.IO) {
    val someString = Preferences.someString.read()
    val someBool = Preferences.someBool.read()
}
```

#### 2c) Use the `SettingsModel` in `compose`

```kotlin
val name = Preferences.someString.collectAsState()
val name = Preferences.someString.collectAsStateWithLifecycle()

// simply use the state inside your composables, the state will change whenever the setting behind it will change

val name = Preferences.someString.asMutableState()

// simple use and even update the state now

```

# :file_folder: Modules

- [Compose Viewer](documentation/Modules/Compose Viewer.md)
- [Feedback](documentation/Modules/Feedback.md)
- [Notification](documentation/Modules/Notification.md)
- [Viewer](documentation/Modules/Viewer.md)

# :sparkles: Demo

A full [demo](/demo) is included inside the demo module, it shows nearly every usage with working examples.

# :information_source: More

- Advanced
  - [Custom Logger](documentation/Advanced/Custom Logger.md)
- Migration
  - [v10](documentation/Migration/v10.md)
  - [v7](documentation/Migration/v7.md)

# :books: API

Check out the [API documentation](https://MFlisar.github.io/Lumberjack/).

# :bulb: Other Libraries

You can find more libraries (all multiplatform) of mine that all do work together nicely [here](https://mflisar.github.io/Libraries/).
