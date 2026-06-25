import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    sourceSets {
        commonMain.dependencies {}
        androidMain.dependencies {}
        named("desktopMain") {
            dependencies {}
        }
    }
}

extensions.configure<LibraryExtension>("android") {
    namespace = "ru.gorinih.familyshopper.shared"
    compileSdk {
        version = release(36)
    }
    ndkVersion = "28.2.13676358"
    defaultConfig {
        minSdk = 28

        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86_64", "x86")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}