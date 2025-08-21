import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.dependencyOf
import com.michaelflisar.kmplibrary.dependencyOfAll
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build logic plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// targets
val buildTargets = Targets(
    // mobile
    android = true,
    iOS = true,
    // desktop
    windows = true,
    macOS = true,
    // web
    wasm = true
)

val androidNamespace = "com.michaelflisar.lumberjack.demo.shared"

// -------------------
// Setup
// -------------------

kotlin {

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargetsLibrary(buildTargets)

    // -------
    // Sources
    // -------

    sourceSets {


        commonMain.dependencies {

            // Kotlin
            implementation(kotlinx.coroutines.core)

            // Compose
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.core)
            implementation(libs.compose.material.icons.extended)

            // library
            implementation(project(":lumberjack:core"))
            implementation(project(":lumberjack:implementations:lumberjack"))
            implementation(project(":lumberjack:loggers:lumberjack:console"))

        }
    }
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
}


