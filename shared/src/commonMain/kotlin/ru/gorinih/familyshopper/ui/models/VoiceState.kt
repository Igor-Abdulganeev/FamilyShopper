package ru.gorinih.familyshopper.ui.models

/**
 * Created by Igor Abdulganeev on 15.05.2026
 */
data class VoiceWorkedState(
    val fieldText: String = "", //строка распознанного текста
    val isVisible: Boolean = false, // показывать ли иконку распознования
    val isEnabled: Boolean = false // включать ли иконку распознования
)