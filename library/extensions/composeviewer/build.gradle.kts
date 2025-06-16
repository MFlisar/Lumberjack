import com.michaelflisar.kmptemplate.BuildFilePlugin
import com.michaelflisar.kmptemplate.Targets

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
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

val androidNamespace = "com.michaelflisar.lumberjack.extensions.composeviewer"

val buildTargets = Targets(
    // mobile
    android = true,
    iOS = true,
    // desktop
    windows = true,
    macOS = true,
    // web
    wasm = false
)

// -------------------
// Setup
// -------------------

kotlin {

    //-------------
    // Targets
    //-------------

    buildFilePlugin.setupTargets(buildTargets)

    // -------
    // Sources
    // -------

    sourceSets {

        commonMain.dependencies {

            // Kotlin
            implementation(kotlinx.io.core)
			
			// Compose + AndroidX
			implementation(libs.compose.material3)
			implementation(libs.compose.material.icons.core)
			implementation(libs.compose.material.icons.extended)

            // Library
            implementation(project(":lumberjack:core"))
            implementation(project(":lumberjack:implementations:lumberjack"))
            implementation(project(":lumberjack:loggers:lumberjack:file"))

            // Dependencies
            //implementation(deps.kmp.parcelize)

        }

        androidMain.dependencies {

            implementation(androidx.core)

            // Library
            implementation(project(":lumberjack:extensions:feedback"))
        }
    }
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
}

// maven publish configuration
buildFilePlugin.setupMavenPublish()