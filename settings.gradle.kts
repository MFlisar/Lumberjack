dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }
        create("compose") {
            from(files("gradle/compose.versions.toml"))
        }
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
    }
}

// --------------
// App
// --------------

// Core
include(":Lumberjack:Core")
project(":Lumberjack:Core").projectDir = file("library/core")

// Implementations
include(":Lumberjack:Implementations:Lumberjack")
project(":Lumberjack:Implementations:Lumberjack").projectDir = file("library/implementations/lumberjack")
include(":Lumberjack:Implementations:Timber")
project(":Lumberjack:Implementations:Timber").projectDir = file("library/implementations/timber")

// Loggers
include(":Lumberjack:Loggers:Lumberjack:Console")
project(":Lumberjack:Loggers:Lumberjack:Console").projectDir = file("library/loggers/lumberjack/console")
include(":Lumberjack:Loggers:Lumberjack:File")
project(":Lumberjack:Loggers:Lumberjack:File").projectDir = file("library/loggers/lumberjack/file")
include(":Lumberjack:Loggers:Timber:Console")
project(":Lumberjack:Loggers:Timber:Console").projectDir = file("library/loggers/timber/console")
include(":Lumberjack:Loggers:Timber:File")
project(":Lumberjack:Loggers:Timber:File").projectDir = file("library/loggers/timber/file")

// Extensions
include(":Lumberjack:Extensions:Feedback")
project(":Lumberjack:Extensions:Feedback").projectDir = file("library/extensions/feedback")
include(":Lumberjack:Extensions:Notification")
project(":Lumberjack:Extensions:Notification").projectDir = file("library/extensions/notification")
include(":Lumberjack:Extensions:Viewer")
project(":Lumberjack:Extensions:Viewer").projectDir = file("library/extensions/viewer")
include(":Lumberjack:Extensions:ComposeViewer")
project(":Lumberjack:Extensions:ComposeViewer").projectDir = file("library/extensions/composeviewer")

if (!System.getenv().containsKey("JITPACK")) {
    include(":demo")
    project(":demo").projectDir = file("demo")
}

