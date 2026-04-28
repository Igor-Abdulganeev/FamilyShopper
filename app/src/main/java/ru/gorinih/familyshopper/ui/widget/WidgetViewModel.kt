package ru.gorinih.familyshopper.ui.widget

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListObject

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

class WidgetViewModel(
    database: DatabaseRepository
) : ViewModel() {
    val stateList = database.takeLists().map { list -> list.map { it.toUiListObject() } }

}