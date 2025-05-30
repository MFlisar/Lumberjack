import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(libs.plugins.binary.compatibility.validator)
}

// -------------------
// Informations
// -------------------

val description = "a xml viewer for lumberjack"

// Module
val artifactId = "extension-viewer"
val androidNamespace = "com.michaelflisar.lumberjack.extensions.viewer"

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
// DEP = "deps.fastscroller|FastScroller|https://github.com/quiph/RecyclerView-FastScroller"
// PLATFORM_INFO = "(2)"               // defines a comment that will be shown in the documentation for this modules platform support

// -------------------
// Setup
// -------------------

android {

    namespace = androidNamespace

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
    // KotlinX / AndroidX / Google
    // ------------------------

    implementation(androidx.core)
    implementation(androidx.recyclerview)
    implementation(androidx.lifecycle)

    // ------------------------
    // Library
    // ------------------------

    implementation(project(":lumberjack:core"))
    implementation(project(":lumberjack:extensions:feedback"))

    // ------------------------
    // Others
    // ------------------------

    implementation(deps.material)
    implementation(deps.fastscroller)
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
    if (!project.gradle.startParameter.taskNames.any { it.contains("publishToMavenLocal") }) {
        signAllPublications()
    }
}