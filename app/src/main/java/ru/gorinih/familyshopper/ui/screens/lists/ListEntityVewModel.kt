package ru.gorinih.familyshopper.ui.screens.lists

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.usecases.SynchronizeLists
import ru.gorinih.familyshopper.ui.screens.GlassCircleImage
import ru.gorinih.familyshopper.ui.screens.lists.models.UiListsState
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListObject

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

class ListEntityVewModel(
    private val database: DatabaseRepository,
    private val sync: SynchronizeLists,
) : ViewModel() {
    var listsState by mutableStateOf(UiListsState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.takeLists()
                .catch { throwable ->
                    listsState = listsState.copy(error = throwable.localizedMessage, loading = false)
                }
                .onEach { entry ->
                    listsState = listsState.copy(lists = entry.map { it.toUiListObject() }, loading = false)
                }
                .stateIn(viewModelScope)
        }

        val typedList = mapOf<Int, Painter>(
            1 to GlassCircleImage(Color.Green),
            2 to GlassCircleImage(Color.Blue),
            3 to GlassCircleImage(Color.Yellow),
            4 to GlassCircleImage(Color.Red),
        )
        listsState = listsState.copy(typedList = typedList)
    }

    fun onDismiss() {
        listsState = listsState.copy(error = null)
    }

    fun updateList() {
        listsState = listsState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            listsState = try {
                val result = sync()
                if (result.isError) listsState.copy(error = result.textError, loading = false)
                else listsState.copy(loading = false)
            } catch (ex: Throwable) {
                listsState.copy(error = ex.localizedMessage, loading = false)
            }
        }
    }
}