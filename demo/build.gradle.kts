plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {

    namespace = "com.michaelflisar.lumberjack.demo"

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
        targetSdk = app.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compiler.get()
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

    implementation(deps.material)

    implementation(androidx.core)
    implementation(androidx.lifecylce)
    implementation(androidx.constraintlayout)

    // Compose
    implementation(platform(compose.bom))
    implementation(compose.material3)
    implementation(compose.activity)
    implementation(compose.material.extendedicons)
    implementation(compose.drawablepainter)

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
    implementation(deps.composedemobaseactivity)
}
