import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    id("maven-publish")
}

kotlin {

    // Java
    jvm()

    // Android
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.michaelflisar.lumberjack.core.CommonParcelize",
                "-P",
                "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.michaelflisar.lumberjack.core.CommonIgnoredOnParcel"
            )
        }
    }

    // iOS
    macosX64()
    macosArm64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    // -------
    // Sources
    // -------

    sourceSets {

        commonMain.dependencies {

            // Kotlin
            implementation(libs.kotlin)
            implementation(libs.kotlinx.io.core)

            // I/O
            implementation(libs.okio)

        }
    }
}

android {
    namespace = "com.michaelflisar.lumberjack.core"

    compileSdk = app.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
/*
project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "core"
                from(components["release"])
            }
        }
    }
}*/