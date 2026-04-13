package ru.gorinih.familyshopper.data.remote.models

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ShoppedItem

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Serializable
data class ListTagObject(
    @ColumnInfo("tag_name")
    val tagName: String,
    @ColumnInfo("tag_strike")
    val tagStrike: Boolean,
    @ColumnInfo("tag_comment")
    val tagComment: String,
)

fun ShoppedItem.toListTagObject() =
    ListTagObject(
        tagName = this.tagName,
        tagStrike = this.isStrike,
        tagComment = this.tagComment
    )

fun ListTagObject.toShoppedItem() =
    ShoppedItem(
        tagId = this.tagName.first().uppercase(),
        tagName = this.tagName,
        isStrike = this.tagStrike,
        tagComment = this.tagComment,
    )