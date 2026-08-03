package ru.gorinih.familyshopper.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
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
import ru.gorinih.familyshopper.ui.views.GlassCircleImageHolder
import ru.gorinih.familyshopper.ui.screens.dictionary.EditDictionariesViewModel
import ru.gorinih.familyshopper.ui.screens.editlist.EditListViewModel
import ru.gorinih.familyshopper.ui.screens.lists.ListEntityVewModel
import ru.gorinih.familyshopper.ui.views.WidgetNotifier
import ru.gorinih.familyshopper.ui.screens.settings.SettingsViewModel
import ru.gorinih.familyshopper.ui.screens.strikelist.ListStrikeTagsViewModel
import ru.gorinih.familyshopper.ui.widget.WidgetUtils
import ru.gorinih.familyshopper.ui.widget.WidgetViewModel
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizer
import ru.gorinih.familyshopper.voice.FamilyVoiceRecognizerImpl

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

fun koinModule(): Module = module {
    single<WidgetNotifier> { WidgetUtils(context = get()) }






    factory<SynchronizeDictionariesUseCase> {
        SynchronizeDictionariesUseCaseImpl(
            remote = get(),
            database = get(),
            pref = get(),
        )
    }
    factory<UpdateListUseCase> {
        UpdateListUseCaseImpl(
            database = get(),
            remote = get(),
            pref = get(),
            store = get(),
        )
    }
    factory<SynchronizeListsUseCase> {
        SynchronizeListsUseCaseImpl(
            database = get(),
            remote = get(),
            pref = get(),
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
            pref = get(),
        )
    }
    factory<SynchronizeDictionariesGetAllRemoteUseCase> {
        SynchronizeDictionariesGetAllRemoteUseCaseImpl(
            remote = get(),
            database = get(),
            pref = get(),
        )
    }
    factory<UpdateUserUseCase> { UpdateUserUseCaseImpl(pref = get(), remote = get()) }

    single { GlassCircleImageHolder }
    single<FamilyVoiceRecognizer> { FamilyVoiceRecognizerImpl(context = androidContext().applicationContext, preference = get()) }


    viewModel {
        SettingsViewModel(
            pref = get(),
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
            pref = get(),
            voice = get(),
            store = get()
        )
    }
    viewModel { (listUuid: String) ->
        EditListViewModel(
            listUuid = listUuid,
            pref = get(),
            database = get(),
            saveList = get(),
            updateList = get(),
            voice = get(),
            store = get()
        )
    }
    viewModel { ListEntityVewModel(database = get(), sync = get(), delete = get(), pref = get()) }
    viewModel { (listId: String) ->
        ListStrikeTagsViewModel(
            listUuid = listId,
            database = get(),
            updateList = get(),
            pref = get()
        )
    }
    viewModel { WidgetViewModel(database = get(), pref = get()) }
    /*  viewModel { FamilyShopperViewModel(pref = get()) }*/
}