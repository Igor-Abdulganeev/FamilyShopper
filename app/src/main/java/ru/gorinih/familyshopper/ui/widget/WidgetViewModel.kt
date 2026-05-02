package ru.gorinih.familyshopper.ui.widget

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.ui.models.TypeLegendList
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListObject
import ru.gorinih.familyshopper.ui.widget.models.WidgetState

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetViewModel(
    private val database: DatabaseRepository,
    private val pref: StorageRepository
) : ViewModel() {
    val stateList = database.takeLists().map { list -> list.map { it.toUiListObject() } }

    var selectedList by mutableStateOf(WidgetState())
        private set

    private var timePause = 700L
    private var clickTime = 0L

    fun takeUserUuid(): String = pref.getClientUUID()

    fun prepareList(listUuid: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - clickTime > timePause) {
            clickTime = currentTime
            Log.i("GINES","SELECT!")
            viewModelScope.launch(Dispatchers.IO) {
                val data = database.takeUpdatedList(listId = listUuid).toUiListObject()
                val clientUuid = pref.getClientUUID()
                val isEdit =
                    (clientUuid == data.listOwner && data.listLegend != TypeLegendList.VIEW) || (data.listLegend == TypeLegendList.ALL || data.listLegend == TypeLegendList.ADD)
                val list = data.copy(isEdit = isEdit)
                selectedList = selectedList.copy(list = list)
            }
        }
    }
}