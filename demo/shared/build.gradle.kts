import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.configs.library.AndroidLibraryConfig
import com.michaelflisar.kmpdevtools.configs.app.DesktopAppConfig
import com.michaelflisar.kmpdevtools.configs.app.WasmAppConfig
import com.michaelflisar.kmpdevtools.configs.module.AppModuleConfig
import com.michaelflisar.kmpdevtools.setupDependencies
import com.michaelflisar.kmpdevtools.configs.module.LibraryModuleConfig
import com.michaelflisar.kmpdevtools.setupDependencies
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    // org.jetbrains.compose
    alias(libs.plugins.jetbrains.compose)
    // docs, publishing, validation
    // --
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    alias(libs.plugins.buildkonfig)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.readManual(project)

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

val androidConfig = AndroidLibraryConfig.createFromPath(
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    packageName = module.appConfig.namespace
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(Type.STRING, "versionName", module.appConfig.versionName)
        buildConfigField(Type.INT, "versionCode", module.appConfig.versionCode.toString())
        buildConfigField(Type.STRING, "namespace", module.appConfig.namespace)
        buildConfigField(Type.STRING, "appName", module.appConfig.name)
    }
}

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

        commonMain.dependencies {

            // Kotlin
            implementation(libs.jetbrains.kotlinx.coroutines.core)
            implementation(libs.jetbrains.kotlinx.io.core)

            // Compose
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)

            // demo ui composables
            implementation(deps.kmp.democomposables)

            // ------------------------
            // Library
            // ------------------------

            // core
            api(project(":lumberjack:core"))
            api(project(":lumberjack:implementation"))

            // 1) loggers - add the ones you need (must match your selected implementation)
            api(project(":lumberjack:loggers:console"))

            // 2) extensions - they work with any implementation...
            api(project(":lumberjack:extensions:composeviewer"))

        }
    }
}





