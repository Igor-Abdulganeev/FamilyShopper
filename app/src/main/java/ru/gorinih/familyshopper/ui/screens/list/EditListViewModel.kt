package ru.gorinih.familyshopper.ui.screens.list

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
import ru.gorinih.familyshopper.ui.models.ActionTag
import ru.gorinih.familyshopper.ui.models.ShoppingItem
import ru.gorinih.familyshopper.ui.models.TypeShoppedList
import ru.gorinih.familyshopper.ui.models.UiShopping

/**
 * Created by Igor Abdulganeev on 07.04.2026
 */

class EditListViewModel(
    private val listUuid: String = "",
    pref: StorageRepository,
    private val database: DatabaseRepository
) : ViewModel() {

    var shoppedList by mutableStateOf(
        value = UiShopping()
            .copy(
                ownerUuid = pref.getClientUUID(),
                listUuid = listUuid,
                listNameId = R.string.new_list_name
            )
    )
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.takeDictionaries()
                .catch {   }//todo обработка ошибок
                .onEach { list ->
                    shoppedList = shoppedList.copy(listAllTags = list.map { it.tagName }) }
                .stateIn(viewModelScope)
        }
    }

    fun updateLegend(type: TypeShoppedList) {
        shoppedList = shoppedList.copy(listLegend = type.listId)
    }

    fun updateListName(listName: String) {
        shoppedList = shoppedList.copy(listName = listName)
    }

    fun updateTag(addedTagName: String, action: ActionTag) {
        val isExists: Boolean =
            shoppedList.tagNames.findLast { (tagName, _) -> tagName == addedTagName }?.run { true }
                ?: false
        when (action) {
            ActionTag.ADD -> {
                if (!isExists) {
                    val listTags =
                        mutableListOf(ShoppingItem(tagName = addedTagName, isStrike = false))
                    listTags.addAll(shoppedList.tagNames)
                    shoppedList = shoppedList.copy(tagNames = listTags)
                }
            }

            ActionTag.DELETE -> {
                if (isExists) {
                    val listTags = shoppedList.tagNames.filter { it.tagName != addedTagName }
                    shoppedList = shoppedList.copy(tagNames = listTags)
                }
            }

            ActionTag.STRIKE -> {
                if (isExists) {
                    val listTags = shoppedList.tagNames.map {
                        if (it.tagName == addedTagName) it.copy(isStrike = !it.isStrike) else it
                    }.toMutableList()
//                    listTags.sortBy { it.isStrike }
                    shoppedList = shoppedList.copy(tagNames = listTags)
                }
            }
        }
    }

    fun showDictionaries() {
        shoppedList = shoppedList.copy(isDictionary = !shoppedList.isDictionary)
    }
}