import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.android.build.api.dsl.LibraryExtension
import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.google.devtools.ksp")
    alias(libs.plugins.room)
    alias(libs.plugins.buildkonfig)
}

val localProperties = Properties().apply {
    val file = File(rootProject.rootDir, "local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    } else {
        logger.warn("ВНИМАНИЕ: Файл local.properties не найден по пути: ${file.absolutePath}")
    }
}

val isProd = project.hasProperty("prod")

val basePoint = if (isProd) {
    localProperties.getProperty("PROD_POINT") ?: ""
} else {
    localProperties.getProperty("DEV_POINT") ?: ""
}

val baseServer = if (isProd) {
    localProperties.getProperty("PROD_SERVER") ?: ""
} else {
    localProperties.getProperty("DEV_SERVER") ?: ""
}

println("BASE_POINT = $basePoint")
println("BASE_SERVER = $baseServer")

compose.resources {
    publicResClass = true
    generateResClass =
        org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration.Always
}

room {
    schemaDirectory("$projectDir/schemas")
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
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.room.runtime)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore)
            implementation(libs.sqlite.bundled) // room для недроидов
            api(libs.compose.resources)
            implementation(libs.androidx.navigation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.adaptive)
            implementation(libs.compose.material3.icons)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.androidx.splashscreen)
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material)
            implementation("net.java.dev.jna:jna:5.18.1@aar")
            implementation("com.alphacephei:vosk-android:0.3.75@aar")
        }
        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

dependencies {
    configurations.filter { it.name.startsWith("ksp") }.forEach { config ->
        add(config.name, libs.room.compiler)
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

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

buildkonfig {
    packageName = "ru.gorinih.familyshopper"
    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "BASE_POINT", basePoint)
        buildConfigField(FieldSpec.Type.STRING, "BASE_SERVER", baseServer)
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", (!isProd).toString())
    }
}

tasks.register("generateVersionFile") {
    description = "Generate file"
    val vCode = project.property("version.code").toString()
    val vName = project.property("version.name").toString()

    val outputDir = file("src/commonMain/composeResources/files")
    val outputFile = file("$outputDir/version.txt")

    inputs.property("vCode", vCode)
    inputs.property("vName", vName)
    outputs.file(outputFile)
    doLast {
        outputDir.mkdirs()
        outputFile.writeText("$vName.$vCode")
    }
}

tasks.matching {
    it.name.contains("copyNonXmlValueResources")
}.configureEach {
    dependsOn("generateVersionFile")
}