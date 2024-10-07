import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {

    jvm {
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {

                implementation(compose.desktop.currentOs)

                implementation(project(":Lumberjack:Core"))
                implementation(project(":Lumberjack:Implementations:Lumberjack"))

                // loggers
                implementation(project(":Lumberjack:Loggers:Lumberjack:Console"))
                implementation(project(":Lumberjack:Loggers:Lumberjack:File"))

                // extensions
                implementation(project(":Lumberjack:Extensions:ComposeViewer"))
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