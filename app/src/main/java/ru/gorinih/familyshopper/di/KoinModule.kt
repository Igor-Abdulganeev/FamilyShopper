package ru.gorinih.familyshopper.di

import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.gorinih.familyshopper.BuildConfig
import ru.gorinih.familyshopper.data.db.DatabaseRepositoryImpl
import ru.gorinih.familyshopper.data.db.ShopperDatabase
import ru.gorinih.familyshopper.data.db.dao.DictionaryDao
import ru.gorinih.familyshopper.data.db.shopperDatabaseBuilder
import ru.gorinih.familyshopper.data.remote.JsonApi
import ru.gorinih.familyshopper.data.remote.RemoteRepositoryImpl
import ru.gorinih.familyshopper.data.remote.interceptors.LoggerInterceptor
import ru.gorinih.familyshopper.data.services.JsonService
import ru.gorinih.familyshopper.data.services.JsonServiceImpl
import ru.gorinih.familyshopper.data.storage.StorageSharedPreference
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionaries
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesImpl
import ru.gorinih.familyshopper.ui.screens.dictionary.EditDictionariesViewModel
import ru.gorinih.familyshopper.ui.screens.settings.SettingsViewModel
import java.util.concurrent.TimeUnit

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

fun koinModule(): Module = module {

    single<ShopperDatabase> { shopperDatabaseBuilder(get()) }
    single<DictionaryDao> { get<ShopperDatabase>().dictionaryDao() }

    single<StorageRepository> { StorageSharedPreference(get()) }
    single<DatabaseRepository> { DatabaseRepositoryImpl(dictionaryDao = get()) }

    factory<JsonService> { JsonServiceImpl() }
    factory<LoggerInterceptor> { LoggerInterceptor(jsonService = get()) }

    single<OkHttpClient> {
        val okHttp = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.BUILD_TYPE == "debug")
            okHttp.addInterceptor(get<LoggerInterceptor>())
        okHttp.build()
    }

    single<GsonConverterFactory> {
        GsonConverterFactory.create(
            GsonBuilder().setStrictness(Strictness.LENIENT).create()
        )
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("${BuildConfig.BASE_POINT}${BuildConfig.BASE_SERVER}")
            .client(get<OkHttpClient>())
            .addConverterFactory(get<GsonConverterFactory>())
            .build()
    }

    single<JsonApi> {
        val retrofit: Retrofit = get()
        val api: JsonApi = retrofit.create(JsonApi::class.java)
        api
    }

    single<JsonApi> {
        get<Retrofit>().create(JsonApi::class.java)
    }

    factory<RemoteRepository> { RemoteRepositoryImpl(pref = get(), remoteApi = get<JsonApi>()) }

    factory<SynchronizeDictionaries> {
        SynchronizeDictionariesImpl(
            remote = get(),
            database = get()
        )
    }

    viewModel { SettingsViewModel(pref = get()) }
    viewModel { EditDictionariesViewModel(database = get(), syncRemote = get(), pref = get()) }
}