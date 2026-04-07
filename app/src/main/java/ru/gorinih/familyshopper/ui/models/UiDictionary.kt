package ru.gorinih.familyshopper.ui.models

import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

data class UiDictionary(
    val tagId: String,
    val tagNames: List<UiTag>,

    )

data class UiTag(
    val tagName: String,
    val needUpdate: Boolean = false
)

fun DictionaryLocalTag.toUiTag() =
    UiTag(
        tagName = this.tagName,
        needUpdate = this.needUpdate
    )