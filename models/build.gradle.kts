import java.util.UUID

plugins {
    id("com.android.library")
}

android {
    namespace = "ru.gorinih.models"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
    }

    buildFeatures {
        buildConfig = false
    }
    sourceSets {
        getByName("main") {
            assets.srcDirs(
                layout.buildDirectory.dir("generated/assets")
            )
        }
    }
}

tasks.register<DefaultTask>(name = "genUuid") {

    val models = mapOf(
        "ru" to "model-ru",
        "en-us" to "model-en-us"
    )

    val generatedAssetsDir =
        layout.buildDirectory.dir("generated/assets")

    outputs.dir(generatedAssetsDir)

    doLast {
        models.forEach { (_, folder) ->

            val targetDir =
                generatedAssetsDir.get()
                    .dir(folder)
                    .asFile

            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            File(targetDir, "uuid")
                .writeText(UUID.randomUUID().toString())
        }
    }
}

tasks.register("buildProd") {
    dependsOn(":models:genUuid")
    dependsOn(":app:assembleProdReleaseApk")
}