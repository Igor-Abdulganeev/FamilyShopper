package ru.gorinih.familyshopper.ui.models

import familyshopper.shared.generated.resources.Res
import familyshopper.shared.generated.resources.button_text_negative
import familyshopper.shared.generated.resources.button_text_positive
import familyshopper.shared.generated.resources.text_delete_list
import org.jetbrains.compose.resources.StringResource

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */

data class DeletingState(
    val isDelete: Boolean = false,
    val deletedId: String = "",
    val queryText: StringResource = Res.string.text_delete_list,
    val positiveTextId: StringResource = Res.string.button_text_positive,
    val negativeTextId: StringResource = Res.string.button_text_negative,
)
