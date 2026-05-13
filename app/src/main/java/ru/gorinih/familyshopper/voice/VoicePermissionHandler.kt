package ru.gorinih.familyshopper.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Created by Igor Abdulganeev on 12.05.2026
 */

class VoicePermissionHandler(
    private val register: ActivityResultRegistry,
    private val context: Context,
) {

    private var activityResultLauncher: ActivityResultLauncher<String>

    private var onResult: ((Boolean) -> Unit)? = null

    init {
        activityResultLauncher = register.register(
            FAMILY_PERMISSION_VOICE_KEY,
            ActivityResultContracts.RequestPermission()
        ) {
            onResult?.invoke(it)
        }
    }

    fun isVoiceGranted(): Boolean =
        ContextCompat.checkSelfPermission(context, VOICE_PERMISSION) == PackageManager.PERMISSION_GRANTED

    fun requestVoicePermission(callback: (Boolean) -> Unit) {
        when(isVoiceGranted()) {
            true -> callback(true)
            false -> {
                onResult = callback
                activityResultLauncher.launch(VOICE_PERMISSION)
            }
        }
    }

    companion object {
        private const val FAMILY_PERMISSION_VOICE_KEY = "family.shopper.permission.voice"
        private const val VOICE_PERMISSION = Manifest.permission.RECORD_AUDIO
    }
}