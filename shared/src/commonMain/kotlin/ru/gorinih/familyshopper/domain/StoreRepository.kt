package ru.gorinih.familyshopper.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import okio.Path.Companion.toPath
import ru.gorinih.familyshopper.domain.models.LegendList
import java.util.EnumMap

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

expect fun provideDataStorePath(): String

fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath { provideDataStorePath().toPath() }

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

    companion object {
        const val SETTINGS_DATA_STORE = "family_shopper_settings"
        const val COLOR_NAME_SCHEME = "family_shopper_color_name_scheme"
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