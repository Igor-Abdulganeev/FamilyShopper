package ru.gorinih.familyshopper.ui

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ru.gorinih.familyshopper.di.initKoin
import ru.gorinih.familyshopper.BuildKonfig

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class FamilyShopperApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            baseUrl = "${BuildKonfig.BASE_POINT}${BuildKonfig.BASE_SERVER}",
            isDebug = BuildKonfig.DEBUG
        ) {
            androidContext(this@FamilyShopperApplication)
            modules(
                //  koinModule()
            )
        }
    }
}