import com.michaelflisar.kmpgradletools.BuildFilePlugin
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
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

val androidNamespace = "com.michaelflisar.lumberjack.extensions.viewer"

// -------------------
// Setup
// -------------------

dependencies {

    // ------------------------
    // KotlinX / AndroidX / Google
    // ------------------------

    implementation(androidx.core)
    implementation(androidx.recyclerview)
    implementation(androidx.lifecycle)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":lumberjack:core"))
    implementation(project(":lumberjack:extensions:feedback"))

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.material)
    implementation(deps.fastscroller)
}

// android configuration
android {

    buildFeatures {
        viewBinding = true
    }

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
if (buildFilePlugin.checkGradleProperty("publishToMaven") != false)
    buildFilePlugin.setupMavenPublish(
        platform = AndroidSingleVariantLibrary("release", true, true)
    )