package ru.gorinih.familyshopper.data.mappers

import ru.gorinih.familyshopper.data.remote.models.ListObject
import ru.gorinih.familyshopper.data.remote.models.ListVersionInfo
import ru.gorinih.familyshopper.data.remote.models.RemoteDictionary
import ru.gorinih.familyshopper.data.remote.models.toListTagObject
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 30.06.2026
 */
private const val PATH_DICTIONARY_VERSION = "dictionaries_versions/"
private const val PATH_DICTIONARY = "dictionaries/"
private const val PATH_CURRENT_LISTS = "current_lists/"
private const val PATH_CURRENT_LISTS_VERSIONS = "current_lists_versions/"
private const val PATH_USERS = "current_users/"

fun DictionaryRemoteTag.toUpdateRemote(): Map<String, Any?> = mapOf(
    "$PATH_DICTIONARY_VERSION${this.tagId}" to if (this.tagNames.isNotEmpty()) this.tagVersion else null,
    "$PATH_DICTIONARY${this.tagId}" to if (this.tagNames.isNotEmpty()) RemoteDictionary(
        tagVersion = this.tagVersion,
        tagId = this.tagId,
        tagNames = this.tagNames
    ) else null
)

fun ShoppedList.toUpdateRemote(): Map<String, Any> = mapOf(
    "$PATH_CURRENT_LISTS_VERSIONS${this.listId}" to ListVersionInfo(
        listVersion = this.listVersion,
        listLegend = this.listLegend.listId,
        listOwner = this.ownerUuid,
        listDatetime = this.dateTime
    ),
    "$PATH_CURRENT_LISTS${this.listId}" to ListObject(
        listId = this.listId,
        listVersion = this.listVersion,
        listDateTime = this.dateTime,
        listName = this.listName,
        listLegend = this.listLegend.listId,
        listOwner = this.ownerUuid,
        listTo = this.usersUuid.map { it.userUuid },
        listTags = this.tagNames.map { it.toListTagObject() }
    )
)

fun String.toDeleteRemote(): Map<String, Any?> = mapOf(
    "${PATH_CURRENT_LISTS_VERSIONS}${this}" to null,
    "${PATH_CURRENT_LISTS}${this}" to null
)

fun String.toUpdateRemote(name: String): Map<String, String> = mapOf(
    "$PATH_USERS${this}" to name
)
