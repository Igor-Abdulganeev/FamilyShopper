package ru.gorinih.familyshopper.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.di.dataStore
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.LegendList
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType
import java.util.UUID

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

class StorageSharedPreference(
    private val context: Context
) : StorageRepository {
    private val preference: SharedPreferences =
        context.getSharedPreferences(SETTING_FILE_NAME, Context.MODE_PRIVATE)

    override fun getClientUUID(): String =
        preference.getString(CLIENT_UUID, "").takeIf { !it.isNullOrBlank() }
            ?: UUID.randomUUID().toString().also {
                setClientUUID(it)
            }

    override fun setClientUUID(uuid: String) = preference.edit {
        putString(CLIENT_UUID, uuid)
    }

    override fun getGroupUUID(): String = preference.getString(GROUP_UUID, "") ?: ""

    override fun setGroupUUID(uuid: String) = preference.edit {
        putString(GROUP_UUID, uuid)
    }

    override fun getStartedKey(): Boolean = preference.getBoolean(APP_FIRST_TIME, false)

    override fun setStartedKey() {
        preference.edit {
            putBoolean(APP_FIRST_TIME, true)
        }
    }

    override fun getUserName(): String = preference.getString(USER_NAME, "") ?: ""

    override fun setUserName(name: String) = preference.edit {
        putString(USER_NAME, name)
    }

    override fun getBackgroundState(): Boolean = preference.getBoolean(BACKGROUND_STATE, true)

    override fun setBackgroundState(rainbow: Boolean) {
        preference.edit {
            putBoolean(BACKGROUND_STATE, rainbow)
        }
    }

    override fun getTypeList(): Int = preference.getInt(DEFAULT_LIST_STATE, 1)

    override fun setTypeList(type: Int) {
        preference.edit {
            putInt(DEFAULT_LIST_STATE, type)
        }
    }

    override fun getSort(): Pair<SortType, SortDirection> {
        val typeName: String? = preference.getString(SORT_TYPE, null)?.uppercase()
        val directionName: String? = preference.getString(SORT_DIRECTION, null)?.uppercase()
        val type = if (typeName != null) SortType.valueOf(typeName) else SortType.NOTHING
        val direction =
            if (directionName != null) SortDirection.valueOf(directionName) else SortDirection.NOTHING
        return Pair(type, direction)
    }

    override fun setSort(
        type: SortType,
        direction: SortDirection
    ) {
        preference.edit {
            putString(SORT_TYPE, type.name)
            putString(SORT_DIRECTION, direction.name)
        }
    }

    override fun getAuthorFilter(): AuthorFilter =
        preference.getString(FILTER_AUTHOR, null)?.run {
            AuthorFilter.valueOf(this.uppercase())
        } ?: AuthorFilter.ALL

    override fun setAuthorFilter(filter: AuthorFilter) {
        preference.edit {
            putString(FILTER_AUTHOR, filter.name)
        }
    }

    override suspend fun updatePalette(palette: String) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey(COLOR_NAME_SCHEME)] = palette
        }
    }

    override fun paletteFlow(): Flow<String> =
        context.dataStore.data.map { pref ->
            pref[stringPreferencesKey(COLOR_NAME_SCHEME)] ?: ""
        }

    override suspend fun getVoice(): Boolean =
        context.dataStore.data.map { pref ->
            pref[booleanPreferencesKey(VOICE_RECOGNIZER)]
        }.firstOrNull() ?: false

    override fun getVoiceFlow(): Flow<Boolean> =
        context.dataStore.data.map { pref ->
            pref[booleanPreferencesKey(VOICE_RECOGNIZER)] == true
        }

    override suspend fun setVoice(enabled: Boolean) {
        context.dataStore.edit { pref ->
            pref[booleanPreferencesKey(VOICE_RECOGNIZER)] = enabled
        }
    }

    override suspend fun setVoiceModel(name: String) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey(VOICE_RECOGNIZER_MODEL)] = name
        }
    }

    override suspend fun getVoiceModel(): String =
        context.dataStore.data.map { pref ->
            pref[stringPreferencesKey(VOICE_RECOGNIZER_MODEL)]
        }.firstOrNull() ?: "model-en-us"

    override fun getVoiceModelFlow(): Flow<String> =
        context.dataStore.data.map { pref ->
            pref[stringPreferencesKey(VOICE_RECOGNIZER_MODEL)] ?: "model-en-us"
        }

    override suspend fun setListSaveTags(listSettings: HashMap<LegendList, Boolean>) {
        context.dataStore.edit { pref ->
            for (list in listSettings) {
                    pref[getListKey(list.key)] = list.value
            }
        }
    }

    override fun getListSaveTagsFlow(): Flow<HashMap<LegendList, Boolean>> =
        context.dataStore.data.map { pref ->
            val result: HashMap<LegendList, Boolean> = HashMap()
            for (list in LegendList.entries) {
                result[list] = pref[getListKey(list)] ?: false
            }
            result
        }

    override suspend fun getListSaveTags(): HashMap<LegendList, Boolean> =
        context.dataStore.data.map { pref ->
            val result: HashMap<LegendList, Boolean> = HashMap()
            for (list in LegendList.entries) {
                result[list] = pref[getListKey(list)] ?: false
            }
            result
        }.firstOrNull() ?: HashMap()


    companion object {
        const val WIDGET_LIST = "family_shopper_widget_list"
        const val WIDGET_EDIT = "family_shopper_widget_list_edit"
        const val WIDGET_FORCE_UPDATE = "family_shopper_widget_force_update"

        const val SETTINGS_DATA_STORE = "family_shopper_settings"
        private const val COLOR_NAME_SCHEME = "family_shopper_color_name_scheme"
        private const val VOICE_RECOGNIZER = "family_shopper_voice_recognizer"
        private const val VOICE_RECOGNIZER_MODEL = "family_shopper_voice_recognizer_model"
        private const val TAG_SAVER_LIST_ALL = "family_shopper_tag_saver_all"
        private const val TAG_SAVER_LIST_ADD = "family_shopper_tag_saver_add"
        private const val TAG_SAVER_LIST_VIEW = "family_shopper_tag_saver_view"
        private const val TAG_SAVER_LIST_PRIVATE = "family_shopper_tag_saver_private"

        private const val GROUP_UUID = "family_shopper_uuid_group"
        private const val CLIENT_UUID = "family_shopper_uuid_client"
        private const val APP_FIRST_TIME = "family_shopper_is_first_time"
        private const val USER_NAME = "family_shopper_user_name"
        private const val BACKGROUND_STATE = "family_shopper_background_state"
        private const val DEFAULT_LIST_STATE = "family_shopper_default_list_state"
        private const val SORT_DIRECTION = "family_shopper_sort_direction"
        private const val SORT_TYPE = "family_shopper_sort_type"
        private const val FILTER_AUTHOR = "family_shopper_filter_author"

        private const val SETTING_FILE_NAME = "family_settings"

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