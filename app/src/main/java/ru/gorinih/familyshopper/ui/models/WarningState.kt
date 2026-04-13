package ru.gorinih.familyshopper.ui.models

/**
 * Created by Igor Abdulganeev on 12.04.2026
 */

data class WarningState(
    val isWarning: Boolean = false,
    val textWarning: String = "",
    val resourceWarning: Int = 0,
)