import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
}

// -------------------
// Informations
// -------------------

val description = "a feedback module for lumberjack"

// Module
val artifactId = "extension-feedback"
val androidNamespace = "com.michaelflisar.lumberjack.extensions.feedback"

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
// PLATFORM_INFO = "(1)"               // defines a comment that will be shown in the documentation for this modules platform support

// -------------------
// Setup
// -------------------

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

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    // ------------------------
    // KotlinX / AndroidX / Google
    // ------------------------

    implementation(androidx.core)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":lumberjack:core"))

    // ------------------------
    // Others
    // ------------------------

    val useLiveDependencies = providers.gradleProperty("useLiveDependencies").get().toBoolean()
    if (useLiveDependencies) {
      implementation(deps.feedback)
    } else {
        implementation(project(":feedbackmanager"))
    }
}

mavenPublishing {

    configure(
        AndroidSingleVariantLibrary("release", true, true)
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