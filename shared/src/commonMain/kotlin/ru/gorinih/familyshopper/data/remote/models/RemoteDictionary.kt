package ru.gorinih.familyshopper.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag

/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

@Serializable
data class RemoteDictionary(
    @SerialName("tagVersion")
    val tagVersion: Int,
    @SerialName("tagId")
    val tagId: String,
    @SerialName("tagNames")
    val tagNames: List<String> = emptyList()
)

fun RemoteDictionary.toDictionaryRemoteTags() =
    DictionaryRemoteTag(
        tagId = this.tagId,
        tagVersion = this.tagVersion,
        tagNames = this.tagNames
    )
