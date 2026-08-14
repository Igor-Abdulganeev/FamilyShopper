package ru.gorinih.familyshopper.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.gorinih.familyshopper.ui.views.WidgetNotifier
import ru.gorinih.familyshopper.ui.widget.WidgetUtils
import ru.gorinih.familyshopper.ui.widget.WidgetViewModel

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

fun koinModule(): Module = module {
    single<WidgetNotifier> { WidgetUtils(context = get()) }

    viewModel { WidgetViewModel(database = get(), store = get()) }
}