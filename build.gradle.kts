// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.gradle.maven.publish.plugin) apply false
    alias(libs.plugins.binary.compatibility.validator) apply false
    alias(deps.plugins.kmp.template.gradle.plugin) apply false
}

// exclude all demo projects from CI builds
subprojects {
    if (project.path.contains(":demo:", ignoreCase = true) && System.getenv("CI") == "true") {
        tasks.configureEach {
            enabled = false
        }
    }
}

// ------------------------
// Build mkdocs
// ------------------------

buildscript {
    dependencies {
        classpath(deps.kmp.template.docs)
    }
}

tasks.register("buildDocs") {
    doLast {
        // read env from build-mkdocs.yml
        val generatedDocsDir = project.findProperty("generatedDocsDir") as String? ?: "gen/docs"
        com.michaelflisar.kmptemplate.docs.buildDocs(
            relativePathDocsCustom = "documentation/custom",
            relativePathGeneratedDocsOutput = generatedDocsDir,
            relativeModulesPath = "library",
            relativeDemosPath = "demo",
            customOtherProjectsYamlUrl = "https://raw.githubusercontent.com/MFlisar/kmp-template/refs/heads/main/data/other-projects.yml"
        )

        println("Docs have been build!")
    }
}