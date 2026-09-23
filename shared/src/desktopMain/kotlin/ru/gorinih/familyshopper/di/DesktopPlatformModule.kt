package ru.gorinih.familyshopper.di

import androidx.room.Room
import org.koin.dsl.module
import ru.gorinih.familyshopper.data.db.ShopperDatabase
import ru.gorinih.familyshopper.data.db.ShopperDatabaseConstructor
import ru.gorinih.familyshopper.ui.views.WidgetNotifier
import ru.gorinih.familyshopper.voice.DesktopVoicePlatformServiceImpl
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizer
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizerImpl
import ru.gorinih.familyshopper.voice.VoicePlatformService
import ru.gorinih.familyshopper.widget.WidgetStub
import java.io.File

/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

actual val platformModule = module {

    single<FamilyVoiceRecognizer> { FamilyVoiceRecognizerImpl() }
    single<VoicePlatformService> { DesktopVoicePlatformServiceImpl() }
    single<WidgetNotifier> { WidgetStub() }


    single {
        val dbFile = File(System.getProperty("user.home"), ".family_shopper/family_shopper.db")
        if (!dbFile.parentFile.exists()) {
            dbFile.parentFile.mkdirs()
        }
        Room.databaseBuilder<ShopperDatabase>(
            name = dbFile.absolutePath,
            factory = { ShopperDatabaseConstructor.initialize() }
        )
    }
}