package ru.gorinih.familyshopper.ui.widget.models

import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

data class WidgetItem(
    val listUuid: String = "",
    val listVersion: Int = 0,
    val listName: String = "",
    val listLegend: Int = 1,
    val listOwner: String = "",
    val tags: List<WidgetTagItem> = emptyList()
)

fun ShoppedList.toListWidgetItem(): WidgetItem =
    WidgetItem(
        listUuid = this.listId,
        listVersion = this.listVersion,
        listName = this.listName,
        listLegend = this.listLegend,
        listOwner = this.ownerUuid,
        tags = this.tagNames.map {
            it.toWidgetTagItem()
        },
    )
