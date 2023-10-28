plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

android {

    namespace = "com.michaelflisar.lumberjack.loggers.timber.file"

    compileSdk = app.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
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
    implementation(libs.kotlin.coroutines)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":Lumberjack:Core"))
    implementation(project(":Lumberjack:Implementations:Timber"))

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.slf4j)
    implementation(deps.logback)
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "logger-timber-file"
                from(components["release"])
            }
        }
    }
}