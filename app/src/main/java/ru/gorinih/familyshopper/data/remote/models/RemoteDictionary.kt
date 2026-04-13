package ru.gorinih.familyshopper.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag

/**
 * Created by Igor Abdulganeev on 03.04.2026
 */

@Serializable
data class RemoteDictionary(
    @SerialName("tag_version")
    val tagVersion: Int,
    @SerialName("tag_id")
    val tagId: String,
    @SerialName("tag_names")
    val tagNames: List<String> = emptyList()
)

fun RemoteDictionary.toDictionaryRemoteTags() =
    DictionaryRemoteTag(
        tagId = this.tagId,
        tagVersion = this.tagVersion,
        tagNames = this.tagNames
    )
