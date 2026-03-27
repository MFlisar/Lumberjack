import com.codingfeline.buildkonfig.compiler.FieldSpec.Type
import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.configs.library.AndroidLibraryConfig
import com.michaelflisar.kmpdevtools.configs.app.DesktopAppConfig
import com.michaelflisar.kmpdevtools.configs.app.WasmAppConfig
import com.michaelflisar.kmpdevtools.core.configs.AppConfig
import com.michaelflisar.kmpdevtools.core.configs.Config
import com.michaelflisar.kmpdevtools.core.configs.LibraryConfig
import com.michaelflisar.kmpdevtools.setupDependencies

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
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

val config = Config.read(rootProject)
val libraryConfig = LibraryConfig.read(rootProject)
val appConfig = AppConfig.read(rootProject)

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

val androidConfig = AndroidLibraryConfig.createManualNamespace(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true,
    namespaceAddon = "demo.app.compose"
)

val desktopConfig = DesktopAppConfig(
    mainClass = "com.michaelflisar.demo.MainKt",
    ico = "icon.ico"
)

val wasmConfig = WasmAppConfig(
    moduleName = "demo",
    outputFileName = "demo.js"
)

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    packageName = appConfig.packageName
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs {
        buildConfigField(Type.STRING, "versionName", appConfig.versionName)
        buildConfigField(Type.INT, "versionCode", appConfig.versionCode.toString())
        buildConfigField(Type.STRING, "packageName", appConfig.packageName)
        buildConfigField(Type.STRING, "appName", appConfig.name)
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

kotlin {

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsApp(project, wasmAppConfig = wasmConfig)
    android {
        buildTargets.setupTargetsAndroidLibrary(project, config, libraryConfig, androidConfig, this)
    }

    // ------------------------
    // Source Sets
    // ------------------------

    sourceSets {

        // ------------------------
        // get / create source sets
        // ------------------------

        val iosMain by creating { dependsOn(commonMain.get()) }
        val featureFileMain by creating { dependsOn(commonMain.get()) }

        // ------------------------
        // dependencies between source sets
        // ------------------------

        setupDependencies(buildTargets, sourceSets) {
            featureFileMain supportedBy Platform.LIST_FILE_SUPPORT
        }

        // ------------------------
        // dependencies
        // ------------------------

        featureFileMain.dependencies {
            api(project(":lumberjack:loggers:file"))
        }

        commonMain.dependencies {

            // AndroidX/Compose/Material
            implementation(libs.jetbrains.compose.material3)

            // Modules
            api(project(":demo:shared"))
        }

        jvmMain.dependencies {
            // OS
            implementation(compose.desktop.currentOs)
        }
    }

}

// -------------------
// Configurations
// -------------------

// windows configuration
compose.desktop {
    application {
        BuildFileUtil.setupWindowsApp(
            project = project,
            config = config,
            application = this,
            appConfig = appConfig,
            desktopAppConfig = desktopConfig
        )
    }
}