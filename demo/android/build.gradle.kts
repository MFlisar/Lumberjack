plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {

    namespace = "com.michaelflisar.lumberjack.demo"

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
        targetSdk = app.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    // eventually use local custom signing
    val debugKeyStore = providers.gradleProperty("debugKeyStore").orNull
    if (debugKeyStore != null) {
        signingConfigs {
            getByName("debug") {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = File(debugKeyStore)
                storePassword = "android"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    // ------------------------
    // AndroidX
    // ------------------------

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(androidx.activity.compose)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":lumberjack:core"))

    // normally you only would use either one of them
    implementation(project(":lumberjack:implementations:timber"))
    implementation(project(":lumberjack:implementations:lumberjack"))

    // loggers - add the ones you need (only the ones that match the selected implementation normally)
    implementation(project(":lumberjack:loggers:lumberjack:console"))
    implementation(project(":lumberjack:loggers:lumberjack:file"))
    implementation(project(":lumberjack:loggers:timber:console"))
    implementation(project(":lumberjack:loggers:timber:file"))

    // extensions - they work with any implementation...
    implementation(project(":lumberjack:extensions:notification"))
    implementation(project(":lumberjack:extensions:feedback"))
    implementation(project(":lumberjack:extensions:viewer"))
    implementation(project(":lumberjack:extensions:composeviewer"))

    // a minimal library that provides some useful composables that I use inside demo activities
    implementation(deps.toolbox.android.demo.app) {
        exclude(group = "io.github.mflisar.lumberjack")
    }
}
