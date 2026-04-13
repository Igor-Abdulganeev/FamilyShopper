package ru.gorinih.familyshopper.ui.screens.lists.models

import androidx.compose.ui.graphics.painter.Painter

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class UiListsState(
    val lists: List<UiListObject> = emptyList(), // будет список с БД
    val error: String? = null, // для обработки ошибок
    val typedList: Map<Int, Painter> = emptyMap(),
    val loading: Boolean = true,
)
