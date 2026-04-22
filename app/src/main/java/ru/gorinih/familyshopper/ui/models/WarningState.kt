package ru.gorinih.familyshopper.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.gorinih.familyshopper.domain.models.Results

/**
 * Created by Igor Abdulganeev on 12.04.2026
 */
@Parcelize
data class WarningState(
    val isWarning: Boolean = false,
    val textWarning: String = "",
    val resourceWarning: Int = 0,
    val complete: String = "",
): Parcelable

fun Results.toWarningState() =
    WarningState(
        isWarning = this.isError,
        textWarning = this.textError,
        resourceWarning = this.textErrorResource,
        complete = this.textComplete,
    )