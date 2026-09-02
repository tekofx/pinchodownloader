import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.compose.components.resources)
    implementation(libs.ktor.client.java)

}

compose.desktop {
    application {
        mainClass = "dev.tekofx.pinchodownloader.MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe, TargetFormat.Rpm)

            packageName = "Pincho Downloader"
            packageVersion = "1.1.0"

            windows {
                menu = true
                shortcut = true
                iconFile.set(project.file("src/main/resources/icons/icon.ico"))
            }
        }
    }
}