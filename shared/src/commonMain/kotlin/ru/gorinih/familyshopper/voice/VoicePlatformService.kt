package ru.gorinih.familyshopper.voice

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

/**
 * Created by Igor Abdulganeev on 04.08.2026
 */

interface VoicePlatformService {
    suspend fun getVoice(dataStore: DataStore<Preferences>): Boolean
    fun getVoiceFlow(dataStore: DataStore<Preferences>): Flow<Boolean>
    suspend fun setVoice(dataStore: DataStore<Preferences>, enabled: Boolean)
    suspend fun setVoiceModel(dataStore: DataStore<Preferences>, name: String)
    suspend fun getVoiceModel(dataStore: DataStore<Preferences>): String
    fun getVoiceModelFlow(dataStore: DataStore<Preferences>): Flow<String>

    companion object {
        const val VOICE_RECOGNIZER = "family_shopper_voice_recognizer"
        const val VOICE_RECOGNIZER_MODEL = "family_shopper_voice_recognizer_model"
    }
}