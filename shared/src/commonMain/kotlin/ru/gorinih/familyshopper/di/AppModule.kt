package ru.gorinih.familyshopper.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * Created by Igor Abdulganeev on 25.06.2026
 */
expect val platformModule: Module

fun provideConfig(baseUrl: String, isDebug: Boolean) = module {
    single(named("BASE_URL")) { baseUrl }
    single(named("IS_DEBUG")) { isDebug }
}

val appModule = module {

}

fun initKoin(
    baseUrl: String,
    isDebug: Boolean,
    appDeclaration: KoinAppDeclaration = {}
) {
    startKoin {
        appDeclaration()
        modules(
            platformModule,
            provideConfig(baseUrl, isDebug),
            networkModule,
            databaseModule,
            appModule,
        )
    }
}