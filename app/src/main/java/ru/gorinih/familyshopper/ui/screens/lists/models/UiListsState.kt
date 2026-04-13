package ru.gorinih.familyshopper.ui.screens.lists.models

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class UiListsState(
    val lists: List<UiListObject> = emptyList(), // будет список с БД
    val error: String? = null, // для обработки ошибок
    val loading: Boolean = false,
)
