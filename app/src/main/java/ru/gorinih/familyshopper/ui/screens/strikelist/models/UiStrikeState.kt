package ru.gorinih.familyshopper.ui.screens.strikelist.models

import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.screens.list.models.UiShoppingItem

/**
 * Created by Igor Abdulganeev on 11.04.2026
 */

data class UiStrikeState(
    val tagNames: List<UiShoppingItem> = emptyList(),
    val warning: WarningState = WarningState(),
    val loading: Boolean = false,
    val isEditable: Boolean = false,
)
