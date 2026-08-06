package ru.gorinih.familyshopper.domain

import android.content.Context
import org.koin.mp.KoinPlatformTools
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.SETTINGS_DATA_STORE

/**
 * Created by Igor Abdulganeev on 06.08.2026
 */

actual fun provideDataStorePath(): String {
    val context = KoinPlatformTools.defaultContext().get().get<Context>()
    return context.filesDir.resolve(SETTINGS_DATA_STORE).absolutePath
}