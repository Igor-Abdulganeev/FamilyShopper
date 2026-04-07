package ru.gorinih.familyshopper

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.gorinih.familyshopper.di.koinModule

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class FamilyShopperApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FamilyShopperApplication)
            modules(
                koinModule()
            )

        }

    }
}