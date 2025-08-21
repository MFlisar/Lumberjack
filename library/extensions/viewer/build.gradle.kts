import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.michaelflisar.kmplibrary.BuildFilePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
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

// kotlin configuration
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(buildFilePlugin.javaVersion()))
    }
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
}

// maven publish configuration
if (buildFilePlugin.checkGradleProperty("publishToMaven") != false)
    buildFilePlugin.setupMavenPublish(
        platform = AndroidSingleVariantLibrary("release", true, true)
    )
