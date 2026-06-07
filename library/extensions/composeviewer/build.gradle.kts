import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.setupDependencies

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
    alias(mflisar.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.read(project)

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
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = false
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

    buildTargets.setupTargetsLibrary(module)
    android {
        buildTargets.setupTargetsAndroidLibrary(module, androidConfig, this)
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
        val featureFile by creating { dependsOn(commonMain.get()) }

        val macosMain by creating { dependsOn(commonMain.get()) }
        val iosMain by creating { dependsOn(commonMain.get()) }
        //val notAndroidMain by creating { dependsOn(commonMain.get()) }

        val platformFeedbackSupport = listOf(Platform.ANDROID, Platform.IOS)

        setupDependencies(module, buildTargets, sourceSets) {

            featureFeedbackSupported supportedBy platformFeedbackSupport
            featureFeedbackNotSupported supportedBy !platformFeedbackSupport
            featureFile supportedBy Platform.LIST_FILE_SUPPORT

            macosMain supportedBy Platform.MACOS
            iosMain supportedBy Platform.IOS
            //notAndroidMain supportedBy !Platform.ANDROID

        }

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
            implementation(mflisar.kmp.platformcontext.core)

            implementation(deps.klip.api)
            implementation(deps.klip.system)

        }

        androidMain.dependencies {
            implementation(mflisar.kmp.platformcontext.initializer)
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
    BuildFileUtil.setupMavenPublish(module)





