package ru.gorinih.familyshopper.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import ru.gorinih.familyshopper.data.storage.StoreRepositoryImpl
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.createDataStore
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
import ru.gorinih.familyshopper.ui.FamilyShopperViewModel
import ru.gorinih.familyshopper.ui.screens.dictionary.EditDictionariesViewModel
import ru.gorinih.familyshopper.ui.screens.editlist.EditListViewModel
import ru.gorinih.familyshopper.ui.screens.lists.ListEntityVewModel
import ru.gorinih.familyshopper.ui.screens.settings.SettingsViewModel
import ru.gorinih.familyshopper.ui.screens.strikelist.ListStrikeTagsViewModel
import ru.gorinih.familyshopper.ui.views.GlassCircleImageHolder

/**
 * Created by Igor Abdulganeev on 25.06.2026
 */
expect val platformModule: Module

fun provideConfig(baseUrl: String, isDebug: Boolean) = module {
    single(named("BASE_URL")) { baseUrl }
    single(named("IS_DEBUG")) { isDebug }
}

val appModule = module {
    single<DataStore<Preferences>> { createDataStore() }

    single<StoreRepository> { StoreRepositoryImpl(dataStore = get(), voiceService = get()) }

    factory<SynchronizeDictionariesUseCase> {
        SynchronizeDictionariesUseCaseImpl(
            remote = get(),
            database = get(),
            store = get(),
        )
    }
    factory<UpdateListUseCase> {
        UpdateListUseCaseImpl(
            database = get(),
            remote = get(),
            store = get(),
        )
    }
    factory<SynchronizeListsUseCase> {
        SynchronizeListsUseCaseImpl(
            database = get(),
            remote = get(),
            store = get()
        )
    }
    factory<GetAndUpdateListUseCase> {
        GetAndUpdateListUseCaseImpl(
            database = get(),
            remote = get()
        )
    }
    factory<UpdateUsersUseCase> { UpdateUsersUseCaseImpl(remote = get(), database = get()) }
    factory<DeleteListUseCase> {
        DeleteListUseCaseImpl(
            database = get(),
            remote = get(),
            store = get()
        )
    }
    factory<SynchronizeDictionariesGetAllRemoteUseCase> {
        SynchronizeDictionariesGetAllRemoteUseCaseImpl(
            remote = get(),
            database = get(),
            store = get(),
        )
    }
    factory<UpdateUserUseCase> { UpdateUserUseCaseImpl(store = get(), remote = get()) }

    single { GlassCircleImageHolder }

    viewModel { FamilyShopperViewModel(pref = get()) }
    viewModel {
        SettingsViewModel(
            remote = get(),
            database = get(),
            updater = get(),
            voice = get(),
            store = get()
        )
    }
    viewModel {
        EditDictionariesViewModel(
            database = get(),
            syncRemote = get(),
            syncAllRemote = get(),
            voice = get(),
            store = get()
        )
    }
    viewModel { (listUuid: String) ->
        EditListViewModel(
            listUuid = listUuid,
            database = get(),
            saveList = get(),
            updateList = get(),
            voice = get(),
            store = get()
        )
    }
    viewModel {
        ListEntityVewModel(
            database = get(),
            sync = get(),
            delete = get(),
            store = get()
        )
    }
    viewModel { (listId: String) ->
        ListStrikeTagsViewModel(
            listUuid = listId,
            database = get(),
            updateList = get(),
            store = get(),
            widgetNotifier = get()
        )
    }

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