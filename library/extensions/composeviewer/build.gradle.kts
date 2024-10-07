import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

// Module
val artifactId = "extension-composeviewer"

// Library
val libraryName = "Lumberjack"
val libraryDescription = "Lumberjack - $artifactId module"
val groupID = "io.github.mflisar.lumberjack"
val release = 2016
val github = "https://github.com/MFlisar/Lumberjack"
val license = "Apache License 2.0"
val licenseUrl = "$github/blob/main/LICENSE"

// -------------------
// Setup
// -------------------

kotlin {

    // Java
    jvm()

    // Android
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // iOS
    //macosX64()
    //macosArm64()
    //iosArm64()
    //iosX64()
    //iosSimulatorArm64()

    // -------
    // Sources
    // -------

    sourceSets {

        commonMain.dependencies {

            // Kotlin
            implementation(libs.kotlin)
            implementation(libs.kotlinx.io.core)
			
			// Compose + AndroidX
			implementation(libs.compose.material3)
			implementation(libs.compose.material.icons.core)
			implementation(libs.compose.material.icons.extended)

            // Library
            implementation(project(":Lumberjack:Core"))
            implementation(project(":Lumberjack:Implementations:Lumberjack"))
            implementation(project(":Lumberjack:Loggers:Lumberjack:File"))

            // Dependencies
            implementation(libs.okio)

        }

        androidMain.dependencies {

            implementation(libs.androidx.core)

            // Library
            implementation(project(":Lumberjack:Extensions:Feedback"))
        }
    }
}

android {
    namespace = "com.michaelflisar.lumberjack.extensions.composeviewer"

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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // Enable GPG signing for all publications
    signAllPublications()
}