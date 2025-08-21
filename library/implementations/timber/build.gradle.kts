import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.michaelflisar.kmplibrary.BuildFilePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.lumberjack.implementation.timber"

// -------------------
// Setup
// -------------------

dependencies {

    // ------------------------
    // Library
    // ------------------------

    api(project(":lumberjack:core"))

    // ------------------------
    // Others
    // ------------------------

    api(deps.timber)
}

// -------------------
// Configurations
// -------------------

// kotlin configuration
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(buildFilePlugin.javaVersion()))
    }
}

// android configuration
android {
    buildFilePlugin.setupAndroidLibrary(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        buildConfig = false
    )
}

// maven publish configuration
buildFilePlugin.setupMavenPublish(
    platform = AndroidSingleVariantLibrary("release", true, true)
)

