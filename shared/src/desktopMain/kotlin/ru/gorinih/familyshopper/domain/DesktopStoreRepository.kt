package ru.gorinih.familyshopper.domain

import ru.gorinih.familyshopper.BuildKonfig
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.SETTINGS_DATA_STORE
import java.io.File

/**
 * Created by Igor Abdulganeev on 06.08.2026
 */

actual fun provideDataStorePath(): String {
    val appName = BuildKonfig.APP_NAME_TO_PATH
    val homeDir = System.getProperty("user.home")
    val dataDir = File(homeDir, ".config/$appName")
    if (!dataDir.exists()) dataDir.mkdirs()
    return File(dataDir, SETTINGS_DATA_STORE).absolutePath
}