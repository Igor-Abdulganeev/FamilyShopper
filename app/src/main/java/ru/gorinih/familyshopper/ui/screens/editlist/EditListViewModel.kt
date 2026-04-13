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
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingState
import ru.gorinih.familyshopper.ui.screens.editlist.models.toShoppedList
import ru.gorinih.familyshopper.ui.screens.editlist.models.toUiShoppingItem
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
        )
    )
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.takeDictionaries()
                .catch { throwable ->
                    shoppedList = shoppedList.copy(error = throwable.localizedMessage)
                }
                .onEach { list ->
                    shoppedList = shoppedList.copy(listAllTags = list.map { it.tagName })
                }
                .stateIn(viewModelScope)
            if (listUuid.isNotBlank()) {
                val dbList = database.takeList(listUuid)
                    .apply {
                        shoppedList = shoppedList.copy(
                            listName = this.listName,
                            listUuid = this.listId,
                            ownerUuid = this.ownerUuid,
                            listVersion = this.listVersion,
                            listLegend = this.listLegend,
                            tagNames = this.tagNames.map { it.toUiShoppingItem() },
                            clientsUuid = this.clientsUuid,
                            loading = false,
                            isAdd = this.ownerUuid == pref.getClientUUID() || (this.ownerUuid != pref.getClientUUID() && this.listLegend < 3), // owner другой и listLegend >2
                            isEdit = this.ownerUuid == pref.getClientUUID() || (this.ownerUuid != pref.getClientUUID() && this.listLegend < 2),
                        )
                    }
                updateList(listUuid)?.let{ list ->
                    list.getNewerOrNull(dbList)?.let { newList ->

                        with(newList){
                            shoppedList = shoppedList.copy(
                                listName = listName,
                                listUuid = listId,
                                ownerUuid = ownerUuid,
                                listVersion = listVersion,
                                listLegend = listLegend,
                                tagNames = tagNames.map { it.toUiShoppingItem() },
                                clientsUuid = clientsUuid,
                                loading = false,
                                isAdd = ownerUuid == pref.getClientUUID() || (ownerUuid != pref.getClientUUID() && listLegend < 3), // owner другой и listLegend >2
                                isEdit = ownerUuid == pref.getClientUUID() || (ownerUuid != pref.getClientUUID() && listLegend < 2),
                            )
                        }
                    }
                }
            } else {
                shoppedList = shoppedList.copy(
                    ownerUuid = pref.getClientUUID(),
                    listUuid = listUuid,
                    listLegend = 1,
                    loading = false,
                )
            }
        }
    }

    fun updateLegend(type: TypeShoppedList) {
        shoppedList = shoppedList.copy(listLegend = type.listId)
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
        val startedTime = System.currentTimeMillis()
        shoppedList = shoppedList.copy(loading = true, dateTime = startedTime)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = saveList(shoppedList.toShoppedList())
                if (!result.isError) {
                    val delta = 1000L - (System.currentTimeMillis() - startedTime)
                    if (delta > 0) delay(delta) // красотульку добавим, мельтешенье уберем
                    shoppedList = shoppedList.copy(loading = false, saved = true)
                } else {
                    throw IllegalArgumentException(result.textError)
                }
            } catch (ex: Throwable) {
                shoppedList = shoppedList.copy(loading = false, error = ex.localizedMessage)
            }
        }
    }

    fun errorDismiss() {
        shoppedList = shoppedList.copy(error = null)
    }

    fun showDictionaries() {
        shoppedList = shoppedList.copy(isDictionary = !shoppedList.isDictionary)
    }
}