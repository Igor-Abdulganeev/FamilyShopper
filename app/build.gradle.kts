import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("21")
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val vCode = 1
val vName = "1.0"

android {
    namespace = "ru.gorinih.familyshopper"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ru.gorinih.familyshopper"
        minSdk = 28
        targetSdk = 36
        this.versionCode = vCode
        this.versionName = vName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("prod") {
            val path = localProperties.getProperty("KEYSTORE_PATH") ?: error("KEYSTORE_PATH not set")
            storeFile = file(path)
            storePassword = localProperties.getProperty("KEYSTORE_PASSWORD") ?: error("KEYSTORE_PASSWORD not set")
            keyAlias = localProperties.getProperty("KEY_ALIAS") ?: error("KEY_ALIAS not set")
            keyPassword = localProperties.getProperty("KEY_PASSWORD") ?: error("KEY_PASSWORD not set")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            isDebuggable = true
            isMinifyEnabled = false
        }

        release {
            isDebuggable = false

            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("prod")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".test"
            versionNameSuffix = "-test"

            val pointUrl = localProperties.getProperty("DEV_POINT")
            val serverUrl = localProperties.getProperty("DEV_SERVER")
            buildConfigField("String", "BASE_POINT", "\"$pointUrl\"")
            buildConfigField("String", "BASE_SERVER", "\"$serverUrl\"")

        }

        create("prod") {
            dimension = "env"

            val pointUrl = localProperties.getProperty("PROD_POINT")
            val serverUrl = localProperties.getProperty("PROD_SERVER")
            buildConfigField("String", "BASE_POINT", "\"$pointUrl\"")
            buildConfigField("String", "BASE_SERVER", "\"$serverUrl\"")
        }
    }

    androidComponents {
        onVariants { variant ->
            val appName = "familyshopper"
            val buildType = variant.buildType
            val flavor = variant.flavorName
            val fileName = buildString {
                append(appName)
                append("-")
                append(buildType)
                append("_")
                append("$vName.$vCode")

                if (!flavor.isNullOrEmpty() && flavor != "prod") {
                    append("-")
                    append(flavor)
                }
                append(".apk")
            }

            val variantName =
                "${flavor?.replaceFirstChar { it.uppercaseChar() }}${buildType?.replaceFirstChar { it.uppercaseChar() }}"
            tasks.register("assemble${variantName}Apk") {
                dependsOn("assemble${variantName}")
                doLast {
                    val apkDir =
                        layout.buildDirectory.dir("outputs/apk/${flavor}/${buildType}").get().asFile

                    apkDir.listFiles()?.forEach { file ->
                        if (file.extension == "apk") {
                            val renamedFile = File(file.parent, fileName)
                            file.renameTo(renamedFile)
                        }
                    }
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.navigation)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.gson)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.androidx.firebase.bom))
    implementation(libs.androidx.firebase.database)
    implementation(libs.androidx.firebase.auth)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}