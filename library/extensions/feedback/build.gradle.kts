plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

android {

    namespace = "com.michaelflisar.lumberjack.extensions.feedback"

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

    // ------------------------
    // AndroidX
    // ------------------------

    implementation(androidx.core)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":Lumberjack:Core"))

    // ------------------------
    // Others
    // ------------------------

    val useLiveDependencies = providers.gradleProperty("useLiveDependencies").get().toBoolean()
    if (useLiveDependencies) {
      implementation(deps.feedback)
    } else {
        implementation(project(":FeedbackManager"))
    }
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "extension-feedback"
                from(components["release"])
            }
        }
    }
}