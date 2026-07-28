package ru.gorinih.familyshopper.ui.screens.dictionary.models

import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class UiDictionaryTag(
    val tagName: String,
    val needUpdate: Boolean = false
)

fun DictionaryLocalTag.toUiTag() =
    UiDictionaryTag(
        tagName = this.tagName,
        needUpdate = this.needUpdate
    )