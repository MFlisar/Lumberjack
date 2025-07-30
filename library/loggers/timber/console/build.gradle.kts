import com.michaelflisar.kmpgradletools.BuildFilePlugin
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.lumberjack.loggers.timber.console"

// -------------------
// Setup
// -------------------

dependencies {

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":lumberjack:core"))
    implementation(project(":lumberjack:implementations:timber"))

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.timber)
}

// -------------------
// Configurations
// -------------------

// android configuration
android {
    buildFilePlugin.setupAndroidLibrary(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        buildConfig = false
    )

    kotlinOptions {
        jvmTarget = buildFilePlugin.javaVersion()
    }
}

// maven publish configuration
buildFilePlugin.setupMavenPublish(
    platform = AndroidSingleVariantLibrary("release", true, true)
)