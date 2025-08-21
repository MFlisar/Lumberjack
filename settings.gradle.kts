dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        mavenLocal()
    }

    versionCatalogs {
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("kotlinx") {
            from(files("gradle/kotlinx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/deps.versions.toml"))
        }
    }
}

pluginManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
        mavenLocal()
    }
}

// --------------
// Functions
// --------------

fun includeModule(path: String, name: String) {
    include(name)
    project(name).projectDir = file(path)
}

// --------------
// Library
// --------------

// Core
includeModule("library/core", ":lumberjack:core")

// Lumberjack
includeModule("library/implementations/lumberjack", ":lumberjack:implementations:lumberjack")
includeModule("library/loggers/lumberjack/console", ":lumberjack:loggers:lumberjack:console")
includeModule("library/loggers/lumberjack/file", ":lumberjack:loggers:lumberjack:file")

// Timber
includeModule("library/implementations/timber", ":lumberjack:implementations:timber")
includeModule("library/loggers/timber/console", ":lumberjack:loggers:timber:console")
includeModule("library/loggers/timber/file", ":lumberjack:loggers:timber:file")

// Extensions
includeModule("library/extensions/composeviewer", ":lumberjack:extensions:composeviewer")
includeModule("library/extensions/feedback", ":lumberjack:extensions:feedback")
includeModule("library/extensions/notification", ":lumberjack:extensions:notification")
includeModule("library/extensions/viewer", ":lumberjack:extensions:viewer")

// --------------
// App
// --------------

include(":demo:shared")
include(":demo:app:android")
include(":demo:app:windows")
include(":demo:app:web")