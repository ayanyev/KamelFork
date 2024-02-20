import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    alias(libs.plugins.org.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.org.jetbrains.compose)
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.vanniktech.maven.publish)
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        multiDexEnabled = true
    }

    namespace = "io.kamel.image"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

}

kotlin {
    explicitApi = ExplicitApiMode.Warning

    androidTarget {
        publishAllLibraryVariants()
    }
    jvm("desktop")
    js(IR) {
        browser()
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    macosX64()
    macosArm64()
    applyDefaultHierarchyTemplate()

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(projects.kamelCore)
                api(projects.kamelDecoder.kamelDecoderSvgStd)
                api(projects.kamelDecoder.kamelDecoderImageBitmap)
                api(projects.kamelDecoder.kamelDecoderImageVector)
                implementation(compose.foundation)
                implementation(libs.ktor.client.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val jvmTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation(compose.material)
                implementation(libs.jetbrains.compose.ui.ui.test.junit4)
            }
        }

        val desktopTest by getting {
            dependsOn(jvmTest)
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(compose.desktop.currentOs)
            }
        }

        val androidUnitTest by getting {
            dependsOn(jvmTest)
        }

        jsMain {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        appleMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

    }
}
