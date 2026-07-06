package ru.gorinih.familyshopper

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ru.gorinih.familyshopper.di.initKoin
import ru.gorinih.familyshopper.di.koinModule

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class FamilyShopperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            baseUrl = "${BuildConfig.BASE_POINT}${BuildConfig.BASE_SERVER}",
            isDebug = BuildConfig.DEBUG
        ) {
            androidContext(this@FamilyShopperApplication)
            modules(
                koinModule()
            )
        }
    }
}