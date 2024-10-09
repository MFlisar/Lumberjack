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
include(":Lumberjack:Core")
project(":Lumberjack:Core").projectDir = file("library/core")

// --------------
// Modules
// --------------

// Android + JVM + iOS
include(":Lumberjack:Implementations:Lumberjack")
project(":Lumberjack:Implementations:Lumberjack").projectDir = file("library/implementations/lumberjack")

// Android Only
include(":Lumberjack:Implementations:Timber")
project(":Lumberjack:Implementations:Timber").projectDir = file("library/implementations/timber")

// Android + JVM + iOS
include(":Lumberjack:Loggers:Lumberjack:Console")
project(":Lumberjack:Loggers:Lumberjack:Console").projectDir = file("library/loggers/lumberjack/console")
include(":Lumberjack:Loggers:Lumberjack:File")
project(":Lumberjack:Loggers:Lumberjack:File").projectDir = file("library/loggers/lumberjack/file")

// Android Only
include(":Lumberjack:Loggers:Timber:Console")
project(":Lumberjack:Loggers:Timber:Console").projectDir = file("library/loggers/timber/console")
include(":Lumberjack:Loggers:Timber:File")
project(":Lumberjack:Loggers:Timber:File").projectDir = file("library/loggers/timber/file")

// Android + JVM + iOS
include(":Lumberjack:Extensions:ComposeViewer")
project(":Lumberjack:Extensions:ComposeViewer").projectDir = file("library/extensions/composeviewer")

// Android Only
include(":Lumberjack:Extensions:Feedback")
project(":Lumberjack:Extensions:Feedback").projectDir = file("library/extensions/feedback")
include(":Lumberjack:Extensions:Notification")
project(":Lumberjack:Extensions:Notification").projectDir = file("library/extensions/notification")
include(":Lumberjack:Extensions:Viewer")
project(":Lumberjack:Extensions:Viewer").projectDir = file("library/extensions/viewer")

// --------------
// App
// --------------

include(":demo:android")
include(":demo:desktop")