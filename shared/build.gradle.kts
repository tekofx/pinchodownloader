plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization")
}

compose.resources {
    publicResClass = true
}

kotlin {
    jvm()



    sourceSets {
        commonMain.dependencies {


            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")

            // Coil
            implementation(libs.coil3.compose)
            //implementation(libs.coil3.network.okhttp)
            implementation(libs.coil3.network.ktor)


            //JSON
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

            // Material icons
            implementation(libs.compose.materialIconsExtended)

            // Datetime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")

            // Viewmodel
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.10.0")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

            // Lottie animations
            implementation("io.github.alexzhirkevich:compottie:2.2.2")
            implementation("io.github.alexzhirkevich:compottie-resources:2.2.2")

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}