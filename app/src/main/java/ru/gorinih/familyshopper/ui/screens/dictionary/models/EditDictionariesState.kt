package ru.gorinih.familyshopper.ui.screens.dictionary.models

import ru.gorinih.familyshopper.ui.models.WarningState

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

data class EditDictionariesState (
    val canSync: Boolean = false,
    val list: List<UiDictionary> = emptyList(),
    val warning: WarningState = WarningState(),
    val isLoading: Boolean = false
    )