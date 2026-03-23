import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.library.AndroidLibraryConfig
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.core.configs.Config
import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    // org.jetbrains.compose
    alias(libs.plugins.jetbrains.compose)
    // docs, publishing, validation
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish.base)
    alias(libs.plugins.binary.compatibility.validator)
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)

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

val androidConfig = AndroidLibraryConfig.create(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = false,
    project = project,
    libraryConfig = libraryConfig
)

// -------------------
// Kotlin
// -------------------

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

kotlin {

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsLibrary(project)
    android {
        buildTargets.setupTargetsAndroidLibrary(project, config, libraryConfig, androidConfig, this)
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val featureFeedbackSupported by creating { dependsOn(commonMain.get()) }
        val featureFeedbackNotSupported by creating { dependsOn(commonMain.get()) }
        val platformFeedbackSupport = listOf(Platform.ANDROID, Platform.IOS)

        val featureFile by creating { dependsOn(commonMain.get()) }

        buildTargets.setupDependencies(featureFeedbackSupported, sourceSets, buildTargets, platformFeedbackSupport)
        buildTargets.setupDependencies(featureFeedbackNotSupported, sourceSets, buildTargets, platformFeedbackSupport, platformsNotSupported = true)
        buildTargets.setupDependencies(featureFile, sourceSets, buildTargets, Platform.LIST_FILE_SUPPORT)

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // Kotlin
            implementation(libs.jetbrains.kotlinx.io.core)
			
			// Compose + AndroidX
            implementation(libs.jetbrains.compose.runtime)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)

            // Library
            implementation(project(":lumberjack:core"))
            implementation(project(":lumberjack:implementation"))

            // Dependencies
            //implementation(deps.kmp.parcelize)

        }

        featureFile.dependencies {
            implementation(project(":lumberjack:loggers:file"))
        }

        featureFeedbackSupported.dependencies {
            implementation(project(":lumberjack:extensions:feedback"))
        }
    }
}

// -------------------
// Publish
// -------------------

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false)
    BuildFileUtil.setupMavenPublish(project, config, libraryConfig)





