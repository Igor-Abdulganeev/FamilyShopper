package ru.gorinih.familyshopper.domain

import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.LegendList
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

interface StorageRepository {
    fun getClientUUID(): String

    fun setClientUUID(uuid: String)

    fun getGroupUUID(): String

    fun setGroupUUID(uuid: String)

    fun getStartedKey(): Boolean

    fun setStartedKey()

    fun getUserName(): String

    fun setUserName(name: String)

    fun getBackgroundState(): Boolean

    fun setBackgroundState(rainbow: Boolean)

    fun getTypeList(): Int

    fun setTypeList(type: Int)

    fun getSort(): Pair<SortType, SortDirection>

    fun setSort(type: SortType, direction: SortDirection)

    fun getAuthorFilter(): AuthorFilter

    fun setAuthorFilter(filter: AuthorFilter)

    suspend fun updatePalette(palette: String)

    fun paletteFlow(): Flow<String>

    suspend fun getVoice(): Boolean

    fun getVoiceFlow(): Flow<Boolean>

    suspend fun setVoice(enabled: Boolean)

    suspend fun setVoiceModel(name: String)

    suspend fun getVoiceModel(): String

    fun getVoiceModelFlow(): Flow<String>

    suspend fun setListSaveTags(listSettings: HashMap<LegendList, Boolean>)

    fun getListSaveTagsFlow(): Flow<HashMap<LegendList, Boolean>>

    suspend fun getListSaveTags(): HashMap<LegendList, Boolean>
}