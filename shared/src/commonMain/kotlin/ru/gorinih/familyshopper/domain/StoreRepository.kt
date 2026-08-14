package ru.gorinih.familyshopper.domain

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import okio.Path.Companion.toPath
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.LegendList
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType
import java.util.EnumMap

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

expect fun provideDataStorePath(): String

expect fun provideDataStoreMigration(): List<DataMigration<Preferences>>

fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        migrations = provideDataStoreMigration()
    ) {
        provideDataStorePath().toPath()
    }

interface StoreRepository {

    suspend fun updatePalette(palette: String)

    fun paletteFlow(): Flow<String>

    suspend fun getVoice(): Boolean

    fun getVoiceFlow(): Flow<Boolean>

    suspend fun setVoice(enabled: Boolean)

    suspend fun setVoiceModel(name: String)

    suspend fun getVoiceModel(): String

    fun getVoiceModelFlow(): Flow<String>

    suspend fun setListSaveTags(listSettings: EnumMap<LegendList, Boolean>)

    fun getListSaveTagsFlow(): Flow<EnumMap<LegendList, Boolean>>

    suspend fun getListSaveTags(): EnumMap<LegendList, Boolean>

    suspend fun getClientUUID(): String

    suspend fun setClientUUID(uuid: String)

    suspend fun getGroupUUID(): String

    fun getGroupUuidFlow(): Flow<String>

    suspend fun setGroupUUID(uuid: String)

    suspend fun getUserName(): String

    suspend fun setUserName(name: String)

    suspend fun getStartedKey(): Boolean

    suspend fun setStartedKey()

    suspend fun getBackgroundState(): Boolean

    fun getBackgroundStateFlow(): Flow<Boolean>

    suspend fun setBackgroundState(rainbow: Boolean)

    suspend fun getTypeList(): Int

    suspend fun setTypeList(type: Int)

    fun getSortFlow(): Flow<Pair<SortType, SortDirection>>

    suspend fun setSort(type: SortType, direction: SortDirection)

    suspend fun getAuthorFilter(): AuthorFilter

    fun getAuthorFilterFlow(): Flow<AuthorFilter>

    suspend fun setAuthorFilter(filter: AuthorFilter)

    companion object {
        const val SETTINGS_DATA_STORE = "family_shopper_settings.preferences_pb"
        const val COLOR_NAME_SCHEME = "family_shopper_color_name_scheme"
        const val CLIENT_UUID = "family_shopper_uuid_client"
        const val GROUP_UUID = "family_shopper_uuid_group"
        const val USER_NAME = "family_shopper_user_name"
        const val APP_FIRST_TIME = "family_shopper_is_first_time"
        const val BACKGROUND_STATE = "family_shopper_background_state"
        const val DEFAULT_LIST_STATE = "family_shopper_default_list_state"
        const val SORT_DIRECTION = "family_shopper_sort_direction"
        const val SORT_TYPE = "family_shopper_sort_type"
        const val FILTER_AUTHOR = "family_shopper_filter_author"

        private const val TAG_SAVER_LIST_ALL = "family_shopper_tag_saver_all"
        private const val TAG_SAVER_LIST_ADD = "family_shopper_tag_saver_add"
        private const val TAG_SAVER_LIST_VIEW = "family_shopper_tag_saver_view"
        private const val TAG_SAVER_LIST_PRIVATE = "family_shopper_tag_saver_private"

        fun getListKey(type: LegendList): Preferences.Key<Boolean> =
            booleanPreferencesKey(
                when (type) {
                    LegendList.ALL -> TAG_SAVER_LIST_ALL
                    LegendList.ADD -> TAG_SAVER_LIST_ADD
                    LegendList.VIEW -> TAG_SAVER_LIST_VIEW
                    LegendList.PRIVATE -> TAG_SAVER_LIST_PRIVATE
                }
            )
    }
}