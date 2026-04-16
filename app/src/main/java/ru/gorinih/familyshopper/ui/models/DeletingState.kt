package ru.gorinih.familyshopper.ui.models

import ru.gorinih.familyshopper.R

/**
 * Created by Igor Abdulganeev on 16.04.2026
 */

data class DeletingState(
    val isDelete: Boolean = false,
    val deletedId: String = "",
    val queryText: Int = 0,
    val positiveTextId: Int = R.string.button_text_positive,
    val negativeTextId: Int = R.string.button_text_negative,
)
