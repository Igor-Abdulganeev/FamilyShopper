package ru.gorinih.familyshopper.ui.screens.lists.models

import ru.gorinih.familyshopper.ui.models.DeletingState
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.views.AuthorFilter
import ru.gorinih.familyshopper.ui.views.SortDirection
import ru.gorinih.familyshopper.ui.views.SortType

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class UiListsState(
    val lists: List<UiListObject> = emptyList(), // будет список с БД
    val warning: WarningState = WarningState(), // для обработки ошибок
    val loading: Boolean = false,
    val deleting: DeletingState = DeletingState(), // удаление списка
    val sortDirection: SortDirection = SortDirection.NOTHING, // ортировка списка вверх вниз
    val sortType: SortType = SortType.NOTHING, // сортировка списка по дате, типу
    val filterRule: AuthorFilter = AuthorFilter.ALL, // фильтр по автору
)
