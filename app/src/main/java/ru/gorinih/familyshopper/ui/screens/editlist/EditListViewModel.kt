package ru.gorinih.familyshopper.ui.screens.editlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.R
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.getNewerOrNull
import ru.gorinih.familyshopper.domain.usecases.GetAndUpdateList
import ru.gorinih.familyshopper.domain.usecases.SaveList
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeShoppedList
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
    pref: StorageRepository,
    private val database: DatabaseRepository,
    private val saveList: SaveList,
    private val updateList: GetAndUpdateList,
) : ViewModel() {

    var shoppedList by mutableStateOf(
        value = UiShoppingState().copy(
            listNameId = R.string.new_list_name,
            date = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(System.currentTimeMillis())),
            listLegend = TypeShoppedList.entries.first { it.listId == pref.getTypeList() }
        )
    )
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.takeDictionaries()
                .catch { throwable ->
                    shoppedList = shoppedList.copy(
                        warning = WarningState(
                            isWarning = true,
                            textWarning = throwable.localizedMessage ?: "неизвестная ошибка"
                        )
                    )
                }
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
                            listLegend = TypeShoppedList.entries.first { it.listId == this.listLegend },
                            tagNames = this.tagNames.map { it.toUiShoppingItem() },
                            usersUuid = this.usersUuid.map { it.userUuid },
                            loading = false,
                            isAdd = this.ownerUuid == pref.getClientUUID() || (this.ownerUuid != pref.getClientUUID() && this.listLegend < 3), // owner другой и listLegend >2
                            isEdit = this.ownerUuid == pref.getClientUUID() || (this.ownerUuid != pref.getClientUUID() && this.listLegend < TypeShoppedList.ADD.listId),
                            userName = this.userName,
                            allUsersUuid = allUsers
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
                                listLegend = TypeShoppedList.entries.first { it.listId == this.listLegend },
                                tagNames = tagNames.map { it.toUiShoppingItem() },
                                usersUuid = usersUuid.map { it.userUuid },
                                loading = false,
                                isAdd = ownerUuid == pref.getClientUUID() || (ownerUuid != pref.getClientUUID() && listLegend < 3), // owner другой и listLegend >2
                                isEdit = ownerUuid == pref.getClientUUID() || (ownerUuid != pref.getClientUUID() && listLegend < 2),
                                userName = this.userName,
                            )
                        }
                    }
                }
            } else {
                shoppedList = shoppedList.copy(
                    ownerUuid = pref.getClientUUID(),
                    listUuid = listUuid,
                    loading = false,
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

    fun updateLegend(type: TypeShoppedList) {
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

    fun showUsersSelect() {
        shoppedList = shoppedList.copy(usersSelect = !shoppedList.usersSelect)
    }

    fun saveList() {
        val startedTime = System.currentTimeMillis()
        shoppedList = shoppedList.copy(loading = true, dateTime = startedTime)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = saveList(shoppedList.toShoppedList()).toWarningState()
                if (!result.isWarning) {
                    val delta = 1000L - (System.currentTimeMillis() - startedTime)
                    if (delta > 0) delay(delta) // красотульку добавим, мельтешенье уберем
                    shoppedList = shoppedList.copy(loading = false, saved = true)
                } else {
                    throw IllegalArgumentException(result.textWarning)
                }
            } catch (ex: Throwable) {
                shoppedList = shoppedList.copy(
                    loading = false,
                    warning = WarningState(
                        isWarning = true,
                        textWarning = ex.localizedMessage ?: "неизвестная ошибка"
                    )
                )
            }
        }
    }

    fun onDismiss() {
        shoppedList = shoppedList.copy(warning = WarningState())
    }

    fun showDictionaries() {
        shoppedList = shoppedList.copy(isDictionary = !shoppedList.isDictionary)
    }
}