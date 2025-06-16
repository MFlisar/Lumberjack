import com.michaelflisar.kmptemplate.BuildFilePlugin
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
    alias(deps.plugins.kmp.template.gradle.plugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val androidNamespace = "com.michaelflisar.lumberjack.loggers.timber.file"

// -------------------
// Setup
// -------------------

dependencies {

    // ------------------------
    // KotlinX / AndroidX / Google
    // ------------------------

    implementation(kotlinx.coroutines.core)
    implementation(kotlinx.io.core)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":lumberjack:core"))
    implementation(project(":lumberjack:implementations:timber"))

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.slf4j)
    implementation(deps.logback)
    implementation(deps.okio)
}

// -------------------
// Configurations
// -------------------

// android configuration
android {
    buildFilePlugin.setupAndroid(
        androidNamespace = androidNamespace,
        compileSdk = app.versions.compileSdk,
        minSdk = app.versions.minSdk,
        compose = false,
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
