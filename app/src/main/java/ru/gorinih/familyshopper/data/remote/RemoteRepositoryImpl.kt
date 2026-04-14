package ru.gorinih.familyshopper.data.remote

import ru.gorinih.familyshopper.data.remote.models.ListObject
import ru.gorinih.familyshopper.data.remote.models.ListVersionInfo
import ru.gorinih.familyshopper.data.remote.models.RemoteDictionary
import ru.gorinih.familyshopper.data.remote.models.toDictionaryRemoteTags
import ru.gorinih.familyshopper.data.remote.models.toListRemoteInfo
import ru.gorinih.familyshopper.data.remote.models.toListTagObject
import ru.gorinih.familyshopper.data.remote.models.toShoppedList
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

private const val PATH_DICTIONARY = "dictionaries/"
private const val PATH_DICTIONARY_VERSION = "dictionaries_versions/"
private const val PATH_CURRENT_LISTS = "current_lists/"
private const val PATH_CURRENT_LISTS_VERSIONS = "current_lists_versions/"
private const val PATH_USERS = "current_users/"


class RemoteRepositoryImpl(
    private val remoteApi: JsonApi,
    private val pref: StorageRepository,
) : RemoteRepository {
    override suspend fun getDictionariesVersions(): Map<String, Int> {
        val result = remoteApi.getDictionariesVersions(groupId = pref.getGroupUUID())
        val map = if (result.body() != null && result.code() == 200) result.body() else null
        return map ?: emptyMap()
    }

    override suspend fun getAllDictionaries(): Map<String, DictionaryRemoteTag> {
        val result = remoteApi.getAllDictionaries(groupId = pref.getGroupUUID())
        val map =
            if (result.body() != null && result.code() == 200) result.body()?.map { (key, value) ->
                key to value.toDictionaryRemoteTags()
            }?.toMap() else null
        return map ?: emptyMap()
    }

    override suspend fun getDictionaryById(
        tagId: String
    ): DictionaryRemoteTag {
        val result = remoteApi.getDictionaryById(groupId = pref.getGroupUUID(), tagId = tagId)
        val map = if (result.body() != null && result.code() == 200)
            result.body()?.toDictionaryRemoteTags() else null
        return map ?: DictionaryRemoteTag(tagId = "", tagVersion = 0, tagNames = emptyList())
    }


    override suspend fun updateDictionaryWithVersion(
        updates: List<DictionaryRemoteTag>
    ) {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return
        val maps = mutableMapOf<String, Any?>()
        updates.forEach {
            maps.putAll(it.toUpdateRemote())
        }
        remoteApi.updateSharedData(
            groupId = groupId,
            updates = maps
        )
    }

    override suspend fun deleteDictionaryWithVersion(updates: List<DictionaryRemoteTag>) {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return

    }

    override suspend fun getListsVersions(): Map<String, ListRemoteInfo> {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        val result = remoteApi.getListsVersions(groupId = groupId)
        return result.body()?.mapValues { it.value.toListRemoteInfo() } ?: emptyMap()
     }

    override suspend fun updateListWithVersion(updates: List<ShoppedList>) {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return
        val maps = updates.associate { it.listId to it.toUpdateRemote() }
        remoteApi.updateSharedData(
            groupId = groupId,
            updates = maps
        )
    }

    override suspend fun getAllCurrentLists(): Map<String, ShoppedList> {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        val result = remoteApi.getAllCurrentLists(groupId = groupId)
        return when {
            result.body() != null && result.code() == 200 -> result.body()
                ?.mapValues { entity -> entity.value.toShoppedList() } ?: emptyMap()

            else -> emptyMap()
        }
    }

    override suspend fun getCurrentListById(
        listId: String
    ): ShoppedList? {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return null
        val result = remoteApi.getCurrentListById(groupId = groupId, listId = listId)
        return  result.body()?.toShoppedList()
    }

    override suspend fun setUserName() {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return
        val user = pref.getClientUUID().toUpdateRemote(pref.getUserName())
        remoteApi.updateSharedData(groupId = groupId, updates = user)
    }

    override suspend fun getUsersNames(): Map<String, String> {
        val groupId = pref.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        val result = remoteApi.getAllCurrentUsers(groupId = groupId)
        return result.body() ?: emptyMap()
    }
}

fun String.toUpdateRemote(name: String): Map<String, String> = mapOf(
    "$PATH_USERS${this}" to name
)

fun DictionaryRemoteTag.toUpdateRemote(): Map<String, Any?> = mapOf(
    "$PATH_DICTIONARY_VERSION${this.tagId}" to if (this.tagNames.isNotEmpty()) this.tagVersion else null,
    "$PATH_DICTIONARY${this.tagId}" to if (this.tagNames.isNotEmpty()) RemoteDictionary(
        tagVersion = this.tagVersion,
        tagId = this.tagId,
        tagNames = this.tagNames
    ) else null
)

fun ShoppedList.toUpdateRemote(): Map<String, Any?> = mapOf(
    "$PATH_CURRENT_LISTS_VERSIONS${this.listId}" to ListVersionInfo(
        listVersion = this.listVersion,
        listLegend = this.listLegend,
        listOwner = this.ownerUuid,
        listDatetime = this.dateTime
    ),
    "$PATH_CURRENT_LISTS${this.listId}" to ListObject(
        listId = this.listId,
        listVersion = this.listVersion,
        listDateTime = this.dateTime,
        listName = this.listName,
        listLegend = this.listLegend,
        listOwner = this.ownerUuid,
        listTo = this.usersUuid.map { it.userUuid },
        listTags = this.tagNames.map { it.toListTagObject() }
    )


)
