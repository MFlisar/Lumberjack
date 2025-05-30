import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

val useLiveDependencies = providers.gradleProperty("useLiveDependencies").get().toBoolean()
val dirDev = if (providers.gradleProperty("work").isPresent) "D:/dev" else "M:/dev"

// supabase pw: FhesROEQHbJwMnRe

// Android App + Web + Windows in einem Modul zusammenfassen
// bspw.: https://github.com/Kotlin/kotlin-wasm-compose-template/blob/main/composeApp/build.gradle.kts

kotlin {

    // bei Änderungen folgendes prüfen:
    // - .\src\wasmJsMain\resources\index.html
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "demo"
        val rootDirPath = project.rootDir.path
        browser {
            commonWebpackConfig {
                outputFileName = "demo.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {

        val wasmJsMain by getting {
            dependencies {
                api(libs.compose.material3)

                implementation(project(":lumberjack:core"))
                implementation(project(":lumberjack:implementations:lumberjack"))

                // loggers
                implementation(project(":lumberjack:loggers:lumberjack:console"))

                // extensions
                //implementation(project(":lumberjack:extensions:composeviewer"))
            }
        }
    }
}