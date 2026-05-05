package ru.gorinih.familyshopper.domain

import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType
import ru.gorinih.familyshopper.ui.theme.models.PaletteScheme

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

    suspend fun updatePalette(palette: PaletteScheme)

    fun paletteFlow(): Flow<PaletteScheme>

}