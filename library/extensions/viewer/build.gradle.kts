plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

android {

    namespace = "com.michaelflisar.lumberjack.extensions.viewer"

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        viewBinding = true
    }

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

    // ------------------------
    // AndroidX
    // ------------------------

    implementation(androidx.core)
    implementation(androidx.recyclerview)
    implementation(androidx.lifecycle)

    implementation(deps.material)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":Lumberjack:Core"))
    implementation(project(":Lumberjack:Extensions:Feedback"))

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.fastscroller)
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "extension-viewer"
                from(components["release"])
            }
        }
    }
}