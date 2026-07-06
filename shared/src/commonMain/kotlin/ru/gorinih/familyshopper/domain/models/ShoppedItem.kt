package ru.gorinih.familyshopper.domain.models

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class ShoppedItem(
    val tagId:String,
    val tagName: String,
    val isStrike: Boolean,
    val tagComment: String
)

fun ShoppedItem.toDictionaryLocalTag(needUpdate: Boolean) =
    DictionaryLocalTag(
        tagId = this.tagId,
        tagName = this.tagName,
        needUpdate = needUpdate
    )
