import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

val description = "the core timber based implementation of lumberjack"

// Module
val artifactId = "implementation-timber"
val androidNamespace = "com.michaelflisar.lumberjack.implementation.timber"

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
// GROUP_ID = "timber"             // defines the "grouping" in the documentation this module belongs to
// DEP = "deps.timber|Timber|https://github.com/JakeWharton/timber"
// PLATFORM_INFO = "(6)"               // defines a comment that will be shown in the documentation for this modules platform support

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

    lint {
        abortOnError = false
    }
}

dependencies {

    // ------------------------
    // Library
    // ------------------------

    api(project(":lumberjack:core"))

    // ------------------------
    // Others
    // ------------------------

    api(deps.timber)
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
