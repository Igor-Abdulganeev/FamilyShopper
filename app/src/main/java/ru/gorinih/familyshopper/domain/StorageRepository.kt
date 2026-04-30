package ru.gorinih.familyshopper.domain

import ru.gorinih.familyshopper.domain.models.AuthorFilter
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

    fun setWidget(appWidgetId: Int, listId: String)

    fun removeWidget(appWidgetId: Int)

    fun getWidget(appWidgetId: Int): String
}