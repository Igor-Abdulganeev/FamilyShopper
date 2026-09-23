package ru.gorinih.familyshopper.voice

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.voice.VoicePlatformService.Companion.VOICE_RECOGNIZER
import ru.gorinih.familyshopper.voice.VoicePlatformService.Companion.VOICE_RECOGNIZER_MODEL

/**
 * Created by Igor Abdulganeev on 06.08.2026
 */

class AndroidVoicePlatformServiceImpl : VoicePlatformService {
    override suspend fun getVoice(dataStore: DataStore<Preferences>): Boolean =
        dataStore.data.map { pref ->
            pref[booleanPreferencesKey(VOICE_RECOGNIZER)]
        }.firstOrNull() ?: false

    override fun getVoiceFlow(dataStore: DataStore<Preferences>): Flow<Boolean> =
        dataStore.data.map { pref ->
            pref[booleanPreferencesKey(VOICE_RECOGNIZER)] == true
        }

    override suspend fun setVoice(
        dataStore: DataStore<Preferences>,
        enabled: Boolean
    ) {
        dataStore.edit { pref ->
            pref[booleanPreferencesKey(VOICE_RECOGNIZER)] = enabled
        }
    }

    override suspend fun setVoiceModel(
        dataStore: DataStore<Preferences>,
        name: String
    ) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(VOICE_RECOGNIZER_MODEL)] = name
        }
    }

    override suspend fun getVoiceModel(dataStore: DataStore<Preferences>): String =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(VOICE_RECOGNIZER_MODEL)]
        }.firstOrNull() ?: "model-en-us"

    override fun getVoiceModelFlow(dataStore: DataStore<Preferences>): Flow<String> =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(VOICE_RECOGNIZER_MODEL)] ?: "model-en-us"
        }
}