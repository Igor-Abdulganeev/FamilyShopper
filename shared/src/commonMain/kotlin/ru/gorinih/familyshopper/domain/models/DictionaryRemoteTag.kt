package ru.gorinih.familyshopper.domain.models

import kotlinx.serialization.Serializable

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

@Serializable
data class DictionaryRemoteTag(
    val tagId: String,
    val tagVersion: Int,
    val tagNames: List<String>
)