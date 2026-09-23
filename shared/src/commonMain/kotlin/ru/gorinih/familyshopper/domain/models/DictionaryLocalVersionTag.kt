package ru.gorinih.familyshopper.domain.models

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

data class DictionaryLocalVersionTag(
    val tagId: String,
    val tagVersion: Int,
    val tagNames: List<String>
)