import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.configs.app.AndroidAppConfig
import com.michaelflisar.kmpdevtools.configs.module.AppModuleConfig

plugins {
    // kmp + app/library
    alias(libs.plugins.android.application)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    // org.jetbrains.compose
    //alias(libs.plugins.jetbrains.compose)
    // docs, publishing, validation
    // --
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = AppModuleConfig.readManual(project)

val androidConfig = AndroidAppConfig(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    targetSdk = app.versions.targetSdk
)

// -------------------
// Configurations
// -------------------

// android configuration
android {

    BuildFileUtil.setupAndroidApp(
        appModuleConfig = module,
        androidAppConfig = androidConfig,
        generateResAppName = true,
        buildConfig = false,
        checkDebugKeyStoreProperty = true,
        setupBuildTypesDebugAndRelease = true
    )
}

dependencies {

    coreLibraryDesugaring(libs.desugar)

    // AndroidX/Compose/Material
    implementation(libs.androidx.activity.compose)
    implementation(libs.jetbrains.compose.material3)

    // Library
    implementation(project(":demo:app:compose"))
    implementation(project(":lumberjack:extensions:feedback"))
}
