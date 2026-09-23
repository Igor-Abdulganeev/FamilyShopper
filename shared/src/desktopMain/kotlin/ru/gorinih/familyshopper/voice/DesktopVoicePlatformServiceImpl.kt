package ru.gorinih.familyshopper.voice

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Igor Abdulganeev on 04.08.2026
 */

class DesktopVoicePlatformServiceImpl : VoicePlatformService {
    override suspend fun getVoice(dataStore: DataStore<Preferences>): Boolean = false

    override fun getVoiceFlow(dataStore: DataStore<Preferences>): Flow<Boolean> = flow {
        emit(false)
    }

    override suspend fun setVoice(
        dataStore: DataStore<Preferences>,
        enabled: Boolean
    ) {
    }

    override suspend fun setVoiceModel(
        dataStore: DataStore<Preferences>,
        name: String
    ) {
    }

    override suspend fun getVoiceModel(dataStore: DataStore<Preferences>): String = ""

    override fun getVoiceModelFlow(dataStore: DataStore<Preferences>): Flow<String> = flow {
        emit("")
    }
}