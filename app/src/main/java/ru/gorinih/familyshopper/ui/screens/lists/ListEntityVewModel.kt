package ru.gorinih.familyshopper.ui.screens.lists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.AuthorFilter
import ru.gorinih.familyshopper.domain.models.SortDirection
import ru.gorinih.familyshopper.domain.models.SortType
import ru.gorinih.familyshopper.domain.usecases.DeleteListUseCase
import ru.gorinih.familyshopper.domain.usecases.SynchronizeListsUseCase
import ru.gorinih.familyshopper.ui.models.DeletingState
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListObject
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListsState
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListObject
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListUsers

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

class ListEntityVewModel(
    private val database: DatabaseRepository,
    private val sync: SynchronizeListsUseCase,
    private val delete: DeleteListUseCase,
    private val pref: StorageRepository,
) : ViewModel() {
    var listsState by mutableStateOf(
        UiListsState().copy(
            sortType = pref.getSort().first,
            sortDirection = pref.getSort().second,
            filterRule = pref.getAuthorFilter(),
            isUpdate = pref.getGroupUUID().isNotBlank()
        )
    )
        private set

    private val keepLists = mutableListOf<UiListObject>()

    init {
        val userUuid = pref.getClientUUID()
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                database.takeLists(),
                database.takeUsers()
            ) { list, users ->
                val userMap = users.associateBy { it.userUuid }
                list.map { entity ->
                    val users = entity.usersUuid.mapNotNull { uuid -> userMap[uuid.userUuid] }
                        .map { it.toUiListUsers() }
                    val isEdit =
                        userUuid == entity.ownerUuid || entity.listLegend == TypeLegendList.ALL.listId
                    val isDelete =
                        userUuid == entity.ownerUuid
                    entity.toUiListObject()
                        .copy(listTo = users, isEdit = isEdit, isDelete = isDelete)
                }
            }.collect { list ->
                keepLists.clear()
                keepLists.addAll(list)
                withContext(Dispatchers.Main.immediate) {
                    val filter = pref.getAuthorFilter()
                    filter(filter)
                }
            }
        }
    }

    fun onDismiss() {
        listsState = listsState.copy(warning = WarningState())
    }

    fun updateList() {
        if (!listsState.loading) {
            listsState = listsState.copy(loading = true)
            viewModelScope.launch(Dispatchers.IO) {
                val result = sync()
                listsState = if (result.isError) {
                    listsState.copy(
                        warning = result.toWarningState(),
                        loading = false
                    )
                } else {
                    listsState.copy(loading = false)
                }
            }
        }
    }

    fun startDeleteList(listId: String) {
        if (!listsState.deleting.isDelete) {
            listsState = listsState.copy(
                deleting = DeletingState(
                    isDelete = true,
                    deletedId = listId,
                    queryText = R.string.text_delete_list,
                ),
            )
        }
    }

    fun startLocalDeleteList(listId: String) {
        if (!listsState.localDeleting.isDelete) {
            listsState = listsState.copy(
                localDeleting = DeletingState(
                    isDelete = true,
                    deletedId = listId,
                    queryText = R.string.text_delete_local_list,
                ),
            )
        }
    }

    fun stopDeleteList() {
        listsState = listsState.copy(deleting = DeletingState(), localDeleting = DeletingState())
    }

    fun deleteList(listId: String) {
        stopDeleteList()
        viewModelScope.launch(Dispatchers.IO) {
            val result = delete(listId = listId)
            val list = keepLists.filterNot { it.listId == listId }
            keepLists.clear()
            keepLists.addAll(list)
            if (result.isError) listsState = listsState.copy(
                deleting = DeletingState(),
                warning = result.toWarningState()
            )
        }
    }

    fun localDeleteList(listId: String) {
        stopDeleteList()
        viewModelScope.launch(Dispatchers.IO) {
            database.deleteList(listId)
            val list = keepLists.filterNot { it.listId == listId }
            keepLists.clear()
            keepLists.addAll(list)
        }
    }

    fun sorter(sortType: SortType, sortDirection: SortDirection) {
        sortedList(listsState.lists.toMutableList(), sortType, sortDirection, listsState.filterRule)
    }

    fun filter(filterType: AuthorFilter) {
        val userId = pref.getClientUUID()
        val filterList = when (filterType) {
            AuthorFilter.ALL -> keepLists
            AuthorFilter.MY -> keepLists.filter { it.listOwner == userId }
            AuthorFilter.OTHERS -> keepLists.filterNot { it.listOwner == userId }
        }.toMutableList()
        sortedList(filterList, listsState.sortType, listsState.sortDirection, filterType)
    }

    private fun sortedList(
        list: MutableList<UiListObject>,
        sortType: SortType,
        sortDirection: SortDirection,
        filterType: AuthorFilter
    ) {
        pref.setSort(sortType, sortDirection)
        pref.setAuthorFilter(filterType)
        val updatedList = mutableListOf<UiListObject>()
        when {
            sortType == SortType.DATE && sortDirection == SortDirection.UP -> {
                updatedList.addAll(list.sortedByDescending { it.listDatetimeValue })
            }

            sortType == SortType.DATE && sortDirection == SortDirection.DOWN -> {
                updatedList.addAll(list.sortedBy { it.listDatetimeValue })
            }

            sortType == SortType.TYPE && sortDirection == SortDirection.UP -> {
                updatedList.addAll(list.sortedByDescending { it.listLegend.listId })
            }

            sortType == SortType.TYPE && sortDirection == SortDirection.DOWN -> {
                updatedList.addAll(list.sortedBy { it.listLegend.listId })
            }

            sortType == SortType.NOTHING -> {
                updatedList.addAll(list)
            }

            sortDirection == SortDirection.NOTHING -> {
                updatedList.addAll(list)
            }
        }
        listsState = listsState.copy(
            sortDirection = sortDirection,
            sortType = sortType,
            filterRule = filterType,
            lists = updatedList,
            loading = false,
            isUpdate = pref.getGroupUUID().isNotBlank()
        )
    }
}