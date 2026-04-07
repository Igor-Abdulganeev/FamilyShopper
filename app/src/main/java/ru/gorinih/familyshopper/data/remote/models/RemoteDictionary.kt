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

/*

@Serializable
data class ListVersionInfo(
    @SerialName("owner")
    val owner: String,
    @SerialName("version_list")
    val versionList: Int,
    @SerialName("type")
    val type: Int
)

@Serializable
data class CurrentList(
    @SerialName("owner")
    val owner: String,
    @SerialName("version_list")
    val versionList: Int,
    @SerialName("type")
    val type: Int,
    @SerialName("name")
    val name: String,
    @SerialName("current")
    val current: List<String> = emptyList(),
    @SerialName("deleted")
    val deleted: List<String> = emptyList(),
    @SerialName("purchased")
    val purchased: List<String> = emptyList()
    )
 */
