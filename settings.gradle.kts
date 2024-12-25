dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
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
    }
}

// --------------
// Library
// --------------

// Android + JVM + iOS
include(":lumberjack:core")
project(":lumberjack:core").projectDir = file("library/core")

// --------------
// Modules
// --------------

// Android + JVM + iOS
include(":lumberjack:implementations:lumberjack")
project(":lumberjack:implementations:lumberjack").projectDir = file("library/implementations/lumberjack")

// Android Only
include(":lumberjack:implementations:timber")
project(":lumberjack:implementations:timber").projectDir = file("library/implementations/timber")

// Android + JVM + iOS
include(":lumberjack:loggers:lumberjack:console")
project(":lumberjack:loggers:lumberjack:console").projectDir = file("library/loggers/lumberjack/console")
include(":lumberjack:loggers:lumberjack:file")
project(":lumberjack:loggers:lumberjack:file").projectDir = file("library/loggers/lumberjack/file")

// Android Only
include(":lumberjack:loggers:timber:console")
project(":lumberjack:loggers:timber:console").projectDir = file("library/loggers/timber/console")
include(":lumberjack:loggers:timber:file")
project(":lumberjack:loggers:timber:file").projectDir = file("library/loggers/timber/file")

// Android + JVM + iOS
include(":lumberjack:extensions:composeviewer")
project(":lumberjack:extensions:composeviewer").projectDir = file("library/extensions/composeviewer")

// Android Only
include(":lumberjack:extensions:feedback")
project(":lumberjack:extensions:feedback").projectDir = file("library/extensions/feedback")
include(":lumberjack:extensions:notification")
project(":lumberjack:extensions:notification").projectDir = file("library/extensions/notification")
include(":lumberjack:extensions:viewer")
project(":lumberjack:extensions:viewer").projectDir = file("library/extensions/viewer")

// --------------
// App
// --------------

include(":demo:android")
include(":demo:desktop")