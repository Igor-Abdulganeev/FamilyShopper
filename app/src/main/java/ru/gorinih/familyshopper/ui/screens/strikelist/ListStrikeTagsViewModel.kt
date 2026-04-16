package ru.gorinih.familyshopper.ui.screens.strikelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.Results
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.usecases.SaveList
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.TypeListTags
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
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
    private val saveList: SaveList,
    private val pref: StorageRepository
) : ViewModel() {


    var shoppedList by mutableStateOf(
        UiStrikeState(
            background = pref.getBackgroundState()
        )
    )
        private set

    private var memoryList: ShoppedList? = null
    private var updater: Deferred<Results>? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.observeList(listId = listUuid).collect { listData ->
                if (memoryList == null) {
                    memoryList = listData
                    memoryList?.let {
                        updater = async {
                            try {
                                saveList(it)
                            } catch (_: Throwable) {
                                return@async Results(isError = false, textError = "")
                            }
                        }
                    }
                } else {
                    memoryList = listData
                }
                val ownerUuid = pref.getClientUUID() == listData.ownerUuid
                val isEditable = when {
                    ownerUuid -> true
                    listData.listLegend == TypeLegendList.ALL.listId -> true
                    else -> false
                }
                val legend = TypeLegendList.entries.first { it.listId == listData.listLegend }
                val type = when {
                    ownerUuid -> TypeListTags.STRIKE
                    legend in listOf(TypeLegendList.ALL, TypeLegendList.ADD) -> TypeListTags.STRIKE
                    else -> TypeListTags.VIEW
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
                            typeList = type,
                            listLegend = legend,
                            listName = listName,
                        )
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            updater?.await()
            updater = null
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

    fun onDismiss() {
        shoppedList = shoppedList.copy(warning = WarningState())
    }

    fun updateList() {
        if (!shoppedList.loading) {
            shoppedList = shoppedList.copy(loading = true)
            viewModelScope.launch(Dispatchers.IO) {
                memoryList =
                    memoryList?.copy(tagNames = shoppedList.tagNames.map { it.toShoppedItem() })
                memoryList?.let {
                    shoppedList = try {
                        val result = saveList(it).toWarningState()
                        if (result.isWarning) shoppedList.copy(warning = result, loading = false)
                        else {
                            shoppedList.copy(loading = false)
                        }
                    } catch (ex: Throwable) {
                        shoppedList.copy(
                            warning = WarningState(
                                isWarning = true,
                                textWarning = ex.localizedMessage ?: ""
                            ), loading = false
                        )
                    }
                }
            }
        }
    }

    fun saveChanged() {
        viewModelScope.launch(NonCancellable + Dispatchers.IO) {
            memoryList =
                memoryList?.copy(tagNames = shoppedList.tagNames.map { it.toShoppedItem() })
            memoryList?.let {
                database.updateList(it)
            }
        }
    }

}