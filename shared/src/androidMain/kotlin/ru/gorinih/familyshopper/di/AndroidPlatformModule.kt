package ru.gorinih.familyshopper.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.gorinih.familyshopper.data.db.ShopperDatabase
import ru.gorinih.familyshopper.voice.AndroidVoicePlatformServiceImpl
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizer
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizerImpl
import ru.gorinih.familyshopper.voice.VoicePlatformService


/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

actual val platformModule = module {

    single<FamilyVoiceRecognizer> {
        FamilyVoiceRecognizerImpl(
            context = androidContext().applicationContext,
            preference = get()
        )
    }
    single<VoicePlatformService> { AndroidVoicePlatformServiceImpl() }


    single {
        val contextApplication = androidContext().applicationContext
        val dbFile = contextApplication.getDatabasePath("family_shopper.db")
        Room.databaseBuilder(
            context = contextApplication,
            klass = ShopperDatabase::class.java,
            name = dbFile.absolutePath
        )
    }

}