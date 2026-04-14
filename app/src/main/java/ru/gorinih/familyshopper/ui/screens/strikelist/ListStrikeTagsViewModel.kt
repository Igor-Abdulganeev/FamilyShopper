package ru.gorinih.familyshopper.ui.screens.strikelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.usecases.SynchronizeLists
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem
import ru.gorinih.familyshopper.ui.screens.editlist.models.toShoppedItem
import ru.gorinih.familyshopper.ui.screens.editlist.models.toUiShoppingItem
import ru.gorinih.familyshopper.ui.screens.strikelist.models.UiStrikeState

/**
 * Created by Igor Abdulganeev on 11.04.2026
 */

class ListStrikeTagsViewModel(
    listUuid: String = "",
    private val database: DatabaseRepository,
    private val sync: SynchronizeLists,
    private val pref: StorageRepository
) : ViewModel() {


    var shoppedList by mutableStateOf(
        UiStrikeState()
    )
        private set

    private var memoryList: ShoppedList? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.observeList(listId = listUuid).collect { listData ->
                memoryList = listData
                val ownerUuid = pref.getClientUUID()
                val isEditable = when{
                    listData.ownerUuid == ownerUuid -> true
                    listData.listLegend == 1 -> true
                    else -> false
                }
                val tagNames = listData.tagNames.map { it.toUiShoppingItem() }
                val l = listData.listName.length
                val listName = when {
                    l > 20 -> "${listData.listName.substring(IntRange(0, 20))}..."
                    else -> listData.listName
                }
                withContext(Dispatchers.Main.immediate) {
                    shoppedList =
                        shoppedList.copy(
                            tagNames = tagNames,
                            isEditable = isEditable,
                            listLegend = listData.listLegend,
                            listName = listName
                        )
                }
            }
        }
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

    fun dismissWarning() {
        shoppedList = shoppedList.copy(warning = WarningState())
    }

    fun updateList() {
        shoppedList = shoppedList.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            shoppedList = try {
                val result = sync()
                if (result.isError) shoppedList.copy(warning = WarningState(isWarning = true, textWarning = result.textError), loading = false)
                else shoppedList.copy(loading = false)
            } catch (ex: Throwable) {
                shoppedList.copy(warning = WarningState(isWarning = true, textWarning = ex.localizedMessage ?: ""), loading = false)
            }
        }
    }

    fun saveChanged() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            memoryList = memoryList?.copy(tagNames = shoppedList.tagNames.map { it.toShoppedItem() })
            memoryList?.let {
                database.updateList(it)
            }
        }
    }
}