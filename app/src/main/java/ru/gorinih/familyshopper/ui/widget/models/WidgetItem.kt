package ru.gorinih.familyshopper.ui.widget.models

import ru.gorinih.familyshopper.domain.models.ShoppedItem
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

data class WidgetItem(
    val listUuid: String = "",
    val listVersion: Int = 0,
    val listName: String = "",
    val tags: List<WidgetTagItem> = emptyList()
)

data class WidgetTagItem(
    val tagId: String = "",
    val tagName: String = "",
    val isStrike: Boolean = false,
    val tagComment: String = "",
)

fun ShoppedItem.toWidgetTagItem() =
    WidgetTagItem(
        tagId = this.tagId,
        tagName = this.tagName,
        isStrike = this.isStrike,
        tagComment = this.tagComment
    )

fun ShoppedList.toListWidgetItem(): WidgetItem =
        WidgetItem(
            listUuid = this.listId,
            listVersion = this.listVersion,
            listName = this.listName,
            tags = this.tagNames.map {
                it.toWidgetTagItem()
            },
        )



