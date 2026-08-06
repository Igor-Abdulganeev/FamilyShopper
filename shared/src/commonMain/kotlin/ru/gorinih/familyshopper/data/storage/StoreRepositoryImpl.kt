package ru.gorinih.familyshopper.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.COLOR_NAME_SCHEME
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.getListKey
import ru.gorinih.familyshopper.domain.models.LegendList
import ru.gorinih.familyshopper.voice.VoicePlatformService
import java.util.EnumMap

/**
 * Created by Igor Abdulganeev on 04.08.2026
 */

class StoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val voiceService: VoicePlatformService,
) : StoreRepository {

    override suspend fun updatePalette(palette: String) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(COLOR_NAME_SCHEME)] = palette
        }
    }

    override fun paletteFlow(): Flow<String> =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(COLOR_NAME_SCHEME)] ?: ""
        }

    override suspend fun getVoice(): Boolean = voiceService.getVoice(dataStore)

    override fun getVoiceFlow(): Flow<Boolean> =
        voiceService.getVoiceFlow(dataStore)

    override suspend fun setVoice(enabled: Boolean) {
        voiceService.setVoice(dataStore, enabled)
    }

    override suspend fun setVoiceModel(name: String) {
        voiceService.setVoiceModel(dataStore, name)
    }

    override suspend fun getVoiceModel(): String = voiceService.getVoiceModel(dataStore)

    override fun getVoiceModelFlow(): Flow<String> = voiceService.getVoiceModelFlow(dataStore)

    override suspend fun setListSaveTags(listSettings: EnumMap<LegendList, Boolean>) {
        dataStore.edit { pref ->
            for (list in listSettings) {
                pref[getListKey(list.key)] = list.value
            }
        }
    }

    override fun getListSaveTagsFlow(): Flow<EnumMap<LegendList, Boolean>> =
        dataStore.data.map { pref ->
            val result: EnumMap<LegendList, Boolean> = EnumMap(LegendList::class.java)
            for (list in LegendList.entries) {
                result[list] = pref[getListKey(list)] ?: (list != LegendList.PRIVATE)
            }
            result
        }

    override suspend fun getListSaveTags(): EnumMap<LegendList, Boolean> =
        dataStore.data.map { pref ->
            val result: EnumMap<LegendList, Boolean> = EnumMap(LegendList::class.java)
            for (list in LegendList.entries) {
                result[list] = pref[getListKey(list)] ?: (list != LegendList.PRIVATE)
            }
            result
        }.firstOrNull() ?: EnumMap(LegendList::class.java)
}