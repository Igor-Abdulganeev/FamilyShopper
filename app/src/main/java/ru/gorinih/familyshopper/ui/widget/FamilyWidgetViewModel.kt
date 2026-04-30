package ru.gorinih.familyshopper.ui.widget

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.ui.screens.lists.models.toUiListObject

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

class FamilyWidgetViewModel(
    database: DatabaseRepository,
    private val pref: StorageRepository
) : ViewModel() {
    val stateList = database.takeLists().map { list -> list.map { it.toUiListObject() } }

    fun saveWidget(appWidgetId: Int, listUuid: String) {
        pref.setWidget(appWidgetId, listUuid)
    }
}