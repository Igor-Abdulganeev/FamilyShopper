package ru.gorinih.familyshopper.ui.screens.dictionary.models

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

data class EditDictionariesState (
    val canSync: Boolean = false,
    val list: List<UiDictionary> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = true
    )