package ru.gorinih.familyshopper.ui.screens.strikelist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.models.Results
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.usecases.UpdateListUseCase
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.models.TypeListTags
import ru.gorinih.familyshopper.ui.models.WarningState
import ru.gorinih.familyshopper.ui.models.toWarningState
import ru.gorinih.familyshopper.ui.screens.editlist.models.UiShoppingItem
import ru.gorinih.familyshopper.ui.screens.editlist.models.toShoppedItem
import ru.gorinih.familyshopper.ui.screens.editlist.models.toUiShoppingItem
import ru.gorinih.familyshopper.ui.screens.strikelist.models.UiStrikeState
import ru.gorinih.familyshopper.ui.views.WidgetNotifier

/**
 * Created by Igor Abdulganeev on 11.04.2026
 */

class ListStrikeTagsViewModel(
    listUuid: String = "",
    private val database: DatabaseRepository,
    private val updateList: UpdateListUseCase,
    private val store: StoreRepository,
    private val widgetNotifier: WidgetNotifier
) : ViewModel() {


    var shoppedList by mutableStateOf(
        UiStrikeState(
            listId = listUuid
        )
    )
        private set

    private var memoryList: ShoppedList? = null
    private var updater: Deferred<Results>? = null
    private var hiddenUpdater: Boolean = false


    init {
        viewModelScope.launch(Dispatchers.IO) {
            store.getBackgroundStateFlow()
                .catch {
                    shoppedList = shoppedList.copy(background = false)
                }.onEach { bg ->
                    shoppedList = shoppedList.copy(background = bg)
                }.stateIn(viewModelScope)
            store.getGroupUuidFlow()
                .catch {
                    shoppedList = shoppedList.copy(isUpdate = false)
                }.onEach { uuid ->
                    shoppedList = shoppedList.copy(isUpdate = uuid.isNotBlank())
                }.stateIn(viewModelScope)
            database.observeList(listId = listUuid).collect { listData ->
                if (memoryList == null && store.getGroupUUID().isNotBlank()) {
                    memoryList = listData
                    memoryList?.let {
                        hiddenUpdater = true
                        updater = async { updateList(it) }
                    }
                } else {
                    memoryList = listData
                }
                val ownerUuid = store.getClientUUID() == listData.ownerUuid
                val isEditable = when {
                    ownerUuid -> true
                    listData.listLegend.listId == TypeLegendList.ALL.listId -> true
                    else -> false
                }
                val legend =
                    TypeLegendList.entries.first { it.listId == listData.listLegend.listId }
                val type = when {
                    legend in listOf(TypeLegendList.ALL, TypeLegendList.ADD) -> TypeListTags.STRIKE
                    legend == TypeLegendList.VIEW -> TypeListTags.VIEW
                    ownerUuid && legend == TypeLegendList.PRIVATE -> TypeListTags.STRIKE
                    else -> TypeListTags.VIEW
                }
                val tagNames = listData.tagNames.map { it.toUiShoppingItem() }
                val l = listData.listName.length
                val listName = when {
                    l > 20 -> "${listData.listName.substring(IntRange(0, 20))}..."
                    else -> listData.listName
                }
                val progress = hiddenUpdater.also { hiddenUpdater = false }
                withContext(Dispatchers.Main.immediate) {
                    shoppedList =
                        shoppedList.copy(
                            tagNames = tagNames,
                            isEditable = isEditable,
                            typeList = type,
                            listLegend = legend,
                            listName = listName,
                            hiddenUpdate = progress
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
                    var strike = false
                    val listTags = shoppedList.tagNames.map {
                        if (it.tagName == tName) {
                            strike = !it.isStrike
                            it.copy(isStrike = strike)
                        } else it
                    }.toMutableList()
                    viewModelScope.launch {
                        database.updateTag(
                            listId = shoppedList.listId,
                            tagName = tName,
                            tagStrike = strike
                        )
                    }
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

    fun updatingList() {
        if (!shoppedList.loading) {
            shoppedList = shoppedList.copy(loading = true)
            viewModelScope.launch(Dispatchers.IO) {
                val keepMemory = memoryList
                memoryList =
                    memoryList?.copy(tagNames = shoppedList.tagNames.map { it.toShoppedItem() })
                memoryList?.let {
                    val result = updateList(it)
                    shoppedList = if (result.isError) {
                        memoryList = keepMemory
                        shoppedList.copy(warning = result.toWarningState(), loading = false)
                    } else {
                        shoppedList.copy(loading = false)
                    }
                }
            }
        }
    }

    suspend fun updateIfChanged() {
        var diff = (memoryList?.tagNames?.count() ?: 0) != shoppedList.tagNames.count()
        if (!diff) {
            memoryList?.tagNames?.let { savedList ->
                for (tag in shoppedList.tagNames) {
                    if (!savedList.any { it.tagId == tag.tagId && it.tagName == tag.tagName && it.isStrike == tag.isStrike && it.tagComment == tag.tagComment }) diff =
                        true
                }
            }
        }
        if (diff) saveChanged()
        widgetNotifier.notifyWidgetChanged()
    }

    private suspend fun saveChanged() {
        memoryList =
            memoryList?.copy(tagNames = shoppedList.tagNames.map { it.toShoppedItem() })
        memoryList?.let {
            val result = updateList(it)
            if (result.isError && result.textError.isNotBlank()) database.updateList(it)
        }
    }
}