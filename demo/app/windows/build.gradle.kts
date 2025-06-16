import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {

    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {

                implementation(compose.desktop.currentOs)

                implementation(project(":demo:shared"))

                implementation(project(":lumberjack:core"))
                implementation(project(":lumberjack:implementations:lumberjack"))

                // loggers
                implementation(project(":lumberjack:loggers:lumberjack:console"))
                implementation(project(":lumberjack:loggers:lumberjack:file"))

                // extensions
                implementation(project(":lumberjack:extensions:composeviewer"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.michaelflisar.lumberjack.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "Lumberjack JVM Demo"
            packageVersion = "1.0.0"
        }
    }
}