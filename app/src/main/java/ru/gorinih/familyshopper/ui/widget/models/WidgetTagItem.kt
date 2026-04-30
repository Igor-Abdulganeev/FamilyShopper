package ru.gorinih.familyshopper.ui.widget.models

import ru.gorinih.familyshopper.domain.models.ShoppedItem

/**
 * Created by Igor Abdulganeev on 30.04.2026
 */

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
