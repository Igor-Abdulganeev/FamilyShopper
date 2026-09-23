package ru.gorinih.familyshopper.ui.models

import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 12.04.2026
 */
@Serializable
data class WarningState(
    val isWarning: Boolean = false,
    val textWarning: String = "",
    val isNetworkWarning: Boolean = false,
    val complete: String = "",
)

fun Results.toWarningState() =
    WarningState(
        isWarning = this.isError,
        textWarning = this.textError,
        isNetworkWarning = this.isNetworkError,
        complete = this.textComplete,
    )