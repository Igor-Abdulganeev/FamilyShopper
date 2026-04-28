package ru.gorinih.familyshopper.ui.widget.models

import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 28.04.2026
 */

data class WidgetItem(
    val listUuid: String = "",
    val tagId: String = "",
    val tagName: String = "",
    val isStrike: Boolean = false,
    val tagComment: String = "",
)

fun ShoppedList.toListWidgetItem(): List<WidgetItem> =
    this.tagNames.map {
        WidgetItem(
            listUuid = this.listId,
            tagId = it.tagId,
            tagName = it.tagName,
            isStrike = it.isStrike,
            tagComment = it.tagComment
        )
    }


