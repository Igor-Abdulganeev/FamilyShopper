package ru.gorinih.familyshopper.voice

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Created by Igor Abdulganeev on 12.05.2026
 */

interface VoicePermissionProvide {
    fun isVoiceGranted(): Boolean
    fun requestVoicePermission(callback: (Boolean) -> Unit)
}

val LocalVoicePermission = staticCompositionLocalOf<VoicePermissionProvide> {
    object : VoicePermissionProvide {
        override fun isVoiceGranted(): Boolean = false

        override fun requestVoicePermission(callback: (Boolean) -> Unit) {}
    }
}