package ru.gorinih.familyshopper.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.APP_FIRST_TIME
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.BACKGROUND_STATE
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.CLIENT_UUID
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.COLOR_NAME_SCHEME
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.DEFAULT_LIST_STATE
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.FILTER_AUTHOR
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.GROUP_UUID
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.SORT_DIRECTION
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.SORT_TYPE
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.USER_NAME
import ru.gorinih.familyshopper.domain.StoreRepository.Companion.getListKey
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.LegendList
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType
import ru.gorinih.familyshopper.voice.VoicePlatformService
import java.util.EnumMap
import java.util.UUID

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

    override suspend fun getClientUUID(): String =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(CLIENT_UUID)]
        }.firstOrNull() ?: UUID.randomUUID().toString().also {
            setClientUUID(it)
        }

    override suspend fun setClientUUID(uuid: String) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(CLIENT_UUID)] = uuid
        }
    }

    override suspend fun getGroupUUID(): String =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(GROUP_UUID)]
        }.firstOrNull() ?: ""

    override fun getGroupUuidFlow(): Flow<String> =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(GROUP_UUID)] ?: ""
        }

    override suspend fun setGroupUUID(uuid: String) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(GROUP_UUID)] = uuid
        }
    }

    override suspend fun getUserName(): String =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(USER_NAME)]
        }.firstOrNull() ?: ""

    override suspend fun setUserName(name: String) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(USER_NAME)] = name
        }
    }

    override suspend fun getStartedKey(): Boolean =
        dataStore.data.map { pref ->
            pref[booleanPreferencesKey(APP_FIRST_TIME)]
        }.firstOrNull() ?: false

    override suspend fun setStartedKey() {
        dataStore.edit { pref ->
            pref[booleanPreferencesKey(APP_FIRST_TIME)] = true
        }
    }

    override suspend fun getBackgroundState(): Boolean =
        dataStore.data.map { pref ->
            pref[booleanPreferencesKey(BACKGROUND_STATE)]
        }.firstOrNull() ?: true

    override fun getBackgroundStateFlow(): Flow<Boolean> =
        dataStore.data.map { pref ->
            pref[booleanPreferencesKey(BACKGROUND_STATE)] == true
        }

    override suspend fun setBackgroundState(rainbow: Boolean) {
        dataStore.edit { pref ->
            pref[booleanPreferencesKey(BACKGROUND_STATE)] = rainbow
        }
    }

    override suspend fun getTypeList(): Int =
        dataStore.data.map { pref ->
            pref[intPreferencesKey(DEFAULT_LIST_STATE)]
        }.firstOrNull() ?: 1

    override suspend fun setTypeList(type: Int) {
        dataStore.edit { pref ->
            pref[intPreferencesKey(DEFAULT_LIST_STATE)] = type
        }
    }

    override fun getSortFlow(): Flow<Pair<SortType, SortDirection>> =
        dataStore.data.map { pref ->
            val typeName: String? = pref[stringPreferencesKey(SORT_TYPE)]
            val directionName: String? = pref[stringPreferencesKey(SORT_DIRECTION)]
            val type = if (typeName != null) SortType.valueOf(typeName) else SortType.NOTHING
            val direction =
                if (directionName != null) SortDirection.valueOf(directionName) else SortDirection.NOTHING
            return@map Pair(type, direction)
        }

    override suspend fun setSort(
        type: SortType,
        direction: SortDirection
    ) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(SORT_TYPE)] = type.name
            pref[stringPreferencesKey(SORT_DIRECTION)] = direction.name
        }
    }

    override suspend fun getAuthorFilter(): AuthorFilter =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(FILTER_AUTHOR)]
        }.firstOrNull()?.run {
            AuthorFilter.valueOf(this.uppercase())
        } ?: AuthorFilter.ALL

    override fun getAuthorFilterFlow(): Flow<AuthorFilter> =
        dataStore.data.map { pref ->
            pref[stringPreferencesKey(FILTER_AUTHOR)]?.run { AuthorFilter.valueOf(this.uppercase()) }
                ?: AuthorFilter.ALL
        }

    override suspend fun setAuthorFilter(filter: AuthorFilter) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(FILTER_AUTHOR)] = filter.name
        }
    }
}