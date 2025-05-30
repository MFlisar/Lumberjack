import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
}

// -------------------
// Informations
// -------------------

val description = "a compose viewer for lumberjack"

// Module
val artifactId = "extension-composeviewer"
val androidNamespace = "com.michaelflisar.lumberjack.extensions.composeviewer"

// Library
val libraryName = "Lumberjack"
val libraryDescription = "Lumberjack - $artifactId module - $description"
val groupID = "io.github.mflisar.lumberjack"
val release = 2016
val github = "https://github.com/MFlisar/Lumberjack"
val license = "Apache License 2.0"
val licenseUrl = "$github/blob/main/LICENSE"

// -------------------
// Variables for Documentation Generator
// -------------------

// # DEP is an optional arrays!

// OPTIONAL = "true"                // defines if this module is optional or not
// GROUP_ID = "extensions"             // defines the "grouping" in the documentation this module belongs to
// DEP = "deps.feedback|FeedbackManager|https://github.com/MFlisar/FeedbackManager"
// PLATFORM_INFO = ""               // defines a comment that will be shown in the documentation for this modules platform support

// -------------------
// Setup
// -------------------

kotlin {

    //-------------
    // Mobile
    //-------------

    // Android
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    //-------------
    // Desktop
    //-------------

    // Windows
    jvm()

    // macOS
    macosX64()
    macosArm64()

    // Linux
    // linuxX64()
    // linuxArm64()

    //-------------
    // Web
    //-------------

    // WASM
    //@OptIn(ExperimentalWasmDsl::class)
    //wasmJs() {
    //    nodejs()
    //}

    //-------------
    // JavaScript
    //-------------

    // js()
    // js(IR)

    // -------
    // Sources
    // -------

    sourceSets {

        commonMain.dependencies {

            // Kotlin
            implementation(kotlinx.io.core)
			
			// Compose + AndroidX
			implementation(libs.compose.material3)
			implementation(libs.compose.material.icons.core)
			implementation(libs.compose.material.icons.extended)

            // Library
            implementation(project(":lumberjack:core"))
            implementation(project(":lumberjack:implementations:lumberjack"))
            implementation(project(":lumberjack:loggers:lumberjack:file"))

            // Dependencies
            implementation(deps.moko.parcelize)

        }

        androidMain.dependencies {

            implementation(androidx.core)

            // Library
            implementation(project(":lumberjack:extensions:feedback"))
        }
    }
}

android {
    namespace = androidNamespace

    compileSdk = app.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )

    coordinates(
        groupId = groupID,
        artifactId = artifactId,
        version = System.getenv("TAG")
    )

    pom {
        name.set(libraryName)
        description.set(libraryDescription)
        inceptionYear.set("$release")
        url.set(github)

        licenses {
            license {
                name.set(license)
                url.set(licenseUrl)
            }
        }

        developers {
            developer {
                id.set("mflisar")
                name.set("Michael Flisar")
                email.set("mflisar.development@gmail.com")
            }
        }

        scm {
            url.set(github)
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)

    // Enable GPG signing for all publications
    signAllPublications()
}