import com.michaelflisar.kmpdevtools.SettingsFileUtil
import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig

dependencyResolutionManagement    {

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
// Settings Plugin
// --------------

plugins {
    // version catalogue does not work here!
    id("io.github.mflisar.kmpdevtools.plugins-settings-gradle") version "7.6.0"
}

// --------------
// Library
// --------------

val libraryConfig = LibraryConfig.read(rootProject)
val libraryName = libraryConfig.libraryName()

// Library Modules
SettingsFileUtil.includeModules(settings, libraryName, libraryConfig)
SettingsFileUtil.includeDokkaModule(settings)

// --------------
// App
// --------------

if (System.getenv("CI") != "true") {
    SettingsFileUtil.includeModulesInFolder(settings, "demo")
}