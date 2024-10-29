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
    // Kotlin
    // ------------------------

    implementation(libs.kotlin)

    // ------------------------
    // AndroidX
    // ------------------------

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.androidx.activity.compose)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":Lumberjack:Core"))

    // normally you only would use either one of them
    implementation(project(":Lumberjack:Implementations:Timber"))
    implementation(project(":Lumberjack:Implementations:Lumberjack"))

    // loggers - add the ones you need (only the ones that match the selected implementation normally)
    implementation(project(":Lumberjack:Loggers:Lumberjack:Console"))
    implementation(project(":Lumberjack:Loggers:Lumberjack:File"))
    implementation(project(":Lumberjack:Loggers:Timber:Console"))
    implementation(project(":Lumberjack:Loggers:Timber:File"))

    // extensions - they work with any implementation...
    implementation(project(":Lumberjack:Extensions:Notification"))
    implementation(project(":Lumberjack:Extensions:Feedback"))
    implementation(project(":Lumberjack:Extensions:Viewer"))
    implementation(project(":Lumberjack:Extensions:ComposeViewer"))

    // a minimal library that provides some useful composables that I use inside demo activities
    implementation(libs.toolbox.core)
    implementation(libs.toolbox.ui)
    implementation(libs.toolbox.app)
}
