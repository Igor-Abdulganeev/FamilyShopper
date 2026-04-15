package ru.gorinih.familyshopper.ui.screens.lists.models

import ru.gorinih.familyshopper.ui.models.WarningState

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class UiListsState(
    val lists: List<UiListObject> = emptyList(), // будет список с БД
    val warning: WarningState = WarningState(), // для обработки ошибок
    val loading: Boolean = false,
)
