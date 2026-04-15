package ru.gorinih.familyshopper.ui.models

import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 12.04.2026
 */

data class WarningState(
    val isWarning: Boolean = false,
    val textWarning: String = "",
    val resourceWarning: Int = 0,
)

fun Results.toWarningState() =
    WarningState(
        isWarning = this.isError,
        textWarning = this.textError
    )