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
import ru.gorinih.familyshopper.data.db.dao.ListsDao
import ru.gorinih.familyshopper.data.db.dao.UserDao
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
import ru.gorinih.familyshopper.domain.usecases.DeleteListUseCase
import ru.gorinih.familyshopper.domain.usecases.DeleteListUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.GetAndUpdateListUseCase
import ru.gorinih.familyshopper.domain.usecases.GetAndUpdateListUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesGetAllRemoteUseCase
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesGetAllRemoteUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesUseCase
import ru.gorinih.familyshopper.domain.usecases.SynchronizeDictionariesUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.SynchronizeListsUseCase
import ru.gorinih.familyshopper.domain.usecases.SynchronizeListsUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.UpdateListUseCase
import ru.gorinih.familyshopper.domain.usecases.UpdateListUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.UpdateUserUseCase
import ru.gorinih.familyshopper.domain.usecases.UpdateUserUseCaseImpl
import ru.gorinih.familyshopper.domain.usecases.UpdateUsersUseCase
import ru.gorinih.familyshopper.domain.usecases.UpdateUsersUseCaseImpl
import ru.gorinih.familyshopper.ui.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.screens.dictionary.EditDictionariesViewModel
import ru.gorinih.familyshopper.ui.screens.editlist.EditListViewModel
import ru.gorinih.familyshopper.ui.screens.lists.ListEntityVewModel
import ru.gorinih.familyshopper.ui.screens.settings.SettingsViewModel
import ru.gorinih.familyshopper.ui.screens.strikelist.ListStrikeTagsViewModel
import ru.gorinih.familyshopper.ui.widget.FamilyWidgetViewModel
import java.util.concurrent.TimeUnit

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

fun koinModule(): Module = module {

    single<ShopperDatabase> { shopperDatabaseBuilder(get()) }
    single<DictionaryDao> { get<ShopperDatabase>().dictionaryDao() }
    single<ListsDao> { get<ShopperDatabase>().listDao() }
    single<UserDao> { get<ShopperDatabase>().userDao() }

    single<StorageRepository> { StorageSharedPreference(get()) }
    single<DatabaseRepository> {
        DatabaseRepositoryImpl(
            dictionaryDao = get(),
            listsDao = get(),
            userDao = get()
        )
    }

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
            GsonBuilder()
                .setStrictness(Strictness.LENIENT)
                .serializeNulls()
                .create()
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

    factory<SynchronizeDictionariesUseCase> {
        SynchronizeDictionariesUseCaseImpl(
            remote = get(),
            database = get(),
            pref = get()
        )
    }
    factory<UpdateListUseCase> { UpdateListUseCaseImpl(database = get(), remote = get(), pref = get()) }
    factory<SynchronizeListsUseCase> {
        SynchronizeListsUseCaseImpl(
            context = get(),
            database = get(),
            remote = get(),
            pref = get()
        )
    }
    factory<GetAndUpdateListUseCase> { GetAndUpdateListUseCaseImpl(database = get(), remote = get()) }
    factory<UpdateUsersUseCase> { UpdateUsersUseCaseImpl(remote = get(), database = get()) }
    factory<DeleteListUseCase> { DeleteListUseCaseImpl(database = get(), remote = get(), pref = get()) }
    factory<SynchronizeDictionariesGetAllRemoteUseCase> { SynchronizeDictionariesGetAllRemoteUseCaseImpl(remote = get(), database = get(), pref = get()) }
    factory<UpdateUserUseCase> { UpdateUserUseCaseImpl(pref = get(), remote = get()) }

    single { GlassCircleImageHolder }

    viewModel { SettingsViewModel(pref = get(), remote = get(), database = get(), updater = get()) }
    viewModel { EditDictionariesViewModel(database = get(), syncRemote = get(), syncAllRemote = get(), pref = get()) }
    viewModel { (listUuid: String) -> EditListViewModel(listUuid = listUuid, pref = get(), database = get(), saveList = get(), updateList = get()) }
    viewModel { ListEntityVewModel(database = get(), sync = get(), delete = get(), pref = get()) }
    viewModel { (listId: String) ->
        ListStrikeTagsViewModel(
            listUuid = listId,
            database = get(),
            updateList = get(),
            pref = get()
        )
    }
    viewModel { FamilyWidgetViewModel(database = get(), pref = get()) }
}