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
import ru.gorinih.familyshopper.domain.usecases.DeleteList
import ru.gorinih.familyshopper.domain.usecases.SynchronizeLists
import ru.gorinih.familyshopper.ui.models.DeletingState
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListsState
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListObject
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListUsers

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

class ListEntityVewModel(
    private val database: DatabaseRepository,
    private val sync: SynchronizeLists,
    private val delete: DeleteList,
    pref: StorageRepository,
) : ViewModel() {
    var listsState by mutableStateOf(UiListsState())
        private set

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
                withContext(Dispatchers.Main.immediate) {
                    listsState = listsState.copy(lists = list, loading = false)
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
                listsState = try {
                    val result = sync().toWarningState()
                    if (result.isWarning) listsState.copy(warning = result, loading = false)
                    else listsState.copy(loading = false)
                } catch (ex: Throwable) {
                    listsState.copy(
                        warning = WarningState(
                            isWarning = true,
                            textWarning = ex.localizedMessage ?: "неизвестная ошибка"
                        ), loading = false
                    )
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

    fun stopDeleteList() {
        listsState = listsState.copy(deleting = DeletingState())
    }

    fun deleteList(listId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = delete(listId = listId)
            listsState = when (result.isError) {
                true -> listsState.copy(
                    deleting = DeletingState(),
                    warning = WarningState(
                        isWarning = true,
                        textWarning = "Удаление выполнено локально, ${result.textError}"
                    )
                )

                false -> listsState.copy(deleting = DeletingState())
            }
        }
    }
}