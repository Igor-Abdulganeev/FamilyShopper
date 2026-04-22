package ru.gorinih.familyshopper.ui.screens.editlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.getNewerOrNull
import ru.gorinih.familyshopper.domain.usecases.GetAndUpdateListUseCase
import ru.gorinih.familyshopper.domain.usecases.UpdateListUseCase
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingState
import ru.gorinih.familyshopper.ui.screens.editlist.models.toShoppedList
import ru.gorinih.familyshopper.ui.screens.editlist.models.toUiShoppingItem
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListUsers
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by Igor Abdulganeev on 07.04.2026
 */

class EditListViewModel(
    private val listUuid: String = "",
    private val pref: StorageRepository,
    private val database: DatabaseRepository,
    private val saveList: UpdateListUseCase,
    private val updateList: GetAndUpdateListUseCase,
) : ViewModel() {

    var shoppedList by mutableStateOf(
        value = UiShoppingState().copy(
            listNameId = R.string.new_list_name,
            date = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(System.currentTimeMillis())),
            isLocalJob = pref.getGroupUUID().isBlank()
        )
    )
        private set

    private var currentEditingFieldId: String? = null
    private var onCurrentFocusLost: (() -> Unit)? = null


    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.takeDictionaries()
                .catch { }
                .onEach { list ->
                    shoppedList = shoppedList.copy(listAllTags = list.map { it.tagName })
                }
                .stateIn(viewModelScope)
            database.takeUsers()
                .catch { }
                .onEach { listUuids ->
                    val selectedUsers = shoppedList.usersUuid.associateBy { it }
                    val allUsers = listUuids.filter { it.userUuid != pref.getClientUUID() }
                        .map { it.toUiListUsers(selectedUsers.containsKey(it.userUuid)) }
                    shoppedList = shoppedList.copy(allUsersUuid = allUsers)
                }.stateIn(
                    viewModelScope
                )
            if (listUuid.isNotBlank()) {
                val dbList = database.takeList(listUuid)
                    .apply {
                        val selectedUsers = this.usersUuid.associateBy { it.userUuid }
                        val allUsers =
                            shoppedList.allUsersUuid.filter { it.userUuid != pref.getClientUUID() }
                                .map { it.copy(isSelected = selectedUsers.containsKey(it.userUuid)) }
                        shoppedList = shoppedList.copy(
                            listName = this.listName,
                            listUuid = this.listId,
                            ownerUuid = this.ownerUuid,
                            listVersion = this.listVersion,
                            listLegend = TypeLegendList.entries.first { it.listId == this.listLegend },
                            tagNames = this.tagNames.map { it.toUiShoppingItem() },
                            usersUuid = this.usersUuid.map { it.userUuid },
                            loading = false,
                            userName = this.userName,
                            allUsersUuid = allUsers,
                            isOwner = pref.getClientUUID() == this.ownerUuid
                        )
                    }
                updateList(listUuid)?.let { list ->
                    list.getNewerOrNull(dbList)?.let { newList ->
                        with(newList) {
                            shoppedList = shoppedList.copy(
                                listName = listName,
                                listUuid = listId,
                                ownerUuid = ownerUuid,
                                listVersion = listVersion,
                                listLegend = TypeLegendList.entries.first { it.listId == this.listLegend },
                                tagNames = tagNames.map { it.toUiShoppingItem() },
                                usersUuid = usersUuid.map { it.userUuid },
                                loading = false,
                                userName = this.userName,
                                isOwner = pref.getClientUUID() == this.ownerUuid
                            )
                        }
                    }
                }
            } else {
                shoppedList = shoppedList.copy(
                    ownerUuid = pref.getClientUUID(),
                    listUuid = listUuid,
                    loading = false,
                    isOwner = true,
                    listLegend = TypeLegendList.entries.first { it.listId == pref.getTypeList() }
                )
            }
        }
    }

    fun selectUser(uuid: String) {
        val allUsers = shoppedList.allUsersUuid.toMutableList()
        val selectedUser = shoppedList.usersUuid.toMutableList()
        val changedItem = allUsers.findLast { it.userUuid == uuid } ?: return
        val changedItemIndex = allUsers.indexOf(changedItem)
        val isSelect = !changedItem.isSelected
        when (isSelect) {
            true -> selectedUser.add(uuid)
            false -> selectedUser.remove(uuid)
        }
        val item = changedItem.copy(isSelected = isSelect)
        allUsers.remove(changedItem)
        allUsers.add(changedItemIndex, item)
        shoppedList = shoppedList.copy(usersUuid = selectedUser, allUsersUuid = allUsers)
    }

    fun updateLegend(type: TypeLegendList) {
        shoppedList = shoppedList.copy(listLegend = type)
    }

    fun updateListName(listName: String) {
        shoppedList = shoppedList.copy(listName = listName)
    }

    fun updateTag(addedTagName: String, action: ActionTag, addedComment: String = "") {
        val tName = addedTagName.lowercase()
        val tId = tName.first().uppercase()
        val isExists: Boolean =
            shoppedList.tagNames.findLast { (_, tagName, _, _) -> tagName == tName }?.run { true }
                ?: false
        when (action) {
            ActionTag.ADD -> {
                if (!isExists) {
                    val listTags =
                        mutableListOf(
                            UiShoppingItem(
                                tagId = tId,
                                tagName = tName,
                                isStrike = false,
                                tagComment = addedComment
                            )
                        )
                    listTags.addAll(shoppedList.tagNames)
                    shoppedList = shoppedList.copy(tagNames = listTags)
                }
            }

            ActionTag.DELETE -> {
                if (isExists) {
                    val listTags = shoppedList.tagNames.filter { it.tagName != tName }
                    shoppedList = shoppedList.copy(tagNames = listTags)
                }
            }

            ActionTag.STRIKE -> {
                if (isExists) {
                    val listTags = shoppedList.tagNames.map {
                        if (it.tagName == tName) it.copy(isStrike = !it.isStrike) else it
                    }.toMutableList()
//                    listTags.sortBy { it.isStrike }
                    shoppedList = shoppedList.copy(tagNames = listTags)
                }
            }

            ActionTag.COMMENT -> {
                shoppedList.tagNames.findLast { item -> item.tagName == tName }
                    ?.let { item ->
                        val tagId = shoppedList.tagNames.indexOf(item)
                        val listTags = shoppedList.tagNames.toMutableList()
                        listTags.remove(item)
                        val updatedItem = item.copy(tagComment = addedComment)
                        listTags.add(tagId, updatedItem)
                        shoppedList = shoppedList.copy(tagNames = listTags)
                    }
            }
        }
    }

    fun saveList() {
        if (!shoppedList.loading) {
            val startedTime = System.currentTimeMillis()
            val listTo =
                if (shoppedList.listLegend == TypeLegendList.PRIVATE) emptyList() else shoppedList.usersUuid
            shoppedList =
                shoppedList.copy(loading = true, dateTime = startedTime, usersUuid = listTo)
            viewModelScope.launch(Dispatchers.IO) {
                val result = saveList(shoppedList.toShoppedList())
                shoppedList = if (!result.isError) {
                    shoppedList.copy(loading = false, saved = true)
                } else {
                    shoppedList.copy(
                        loading = false,
                        warning = result.toWarningState()
                    )
                }
            }
        }
    }

    fun onDismiss() {
        shoppedList = shoppedList.copy(warning = WarningState())
    }

    fun onDismissSaved() {
        shoppedList = shoppedList.copy(warning = WarningState(), saved = true)
    }

    fun registerField(fieldId: String, onFocusLost: () -> Unit) {
        if (currentEditingFieldId != null && currentEditingFieldId != fieldId) {
            onCurrentFocusLost?.invoke()
        }
        currentEditingFieldId = fieldId
        onCurrentFocusLost = onFocusLost
    }

    fun unregisterField(fieldId: String) {
        if (currentEditingFieldId == fieldId) {
            currentEditingFieldId = null
            onCurrentFocusLost = null
        }
    }

    fun clearCurrentField() {
        onCurrentFocusLost?.invoke()
        currentEditingFieldId = null
        onCurrentFocusLost = null
    }

}