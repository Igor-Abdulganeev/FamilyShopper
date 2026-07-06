package ru.gorinih.familyshopper.domain.models

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */

data class DictionaryLocalTag(
    val tagId: String,
    val tagName: String,
    val needUpdate: Boolean = false
)