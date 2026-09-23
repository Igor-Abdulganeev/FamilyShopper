package ru.gorinih.familyshopper.ui.screens.settings.models

import kotlinx.serialization.Serializable

/**
 * Created by Igor Abdulganeev on 27.07.2026
 */

@Serializable
data class VoiceState(
    val isVoiceRecognizer: Boolean = false, // включен ли режим ввода голосом
    val voiceRecognizerModel: VoiceModels = VoiceModels.ENGLISH // выбранный движок
)
