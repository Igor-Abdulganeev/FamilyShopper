package ru.gorinih.familyshopper.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import ru.gorinih.familyshopper.data.mappers.toDeleteRemote
import ru.gorinih.familyshopper.data.mappers.toUpdateRemote
import ru.gorinih.familyshopper.data.remote.models.ListObject
import ru.gorinih.familyshopper.data.remote.models.ListVersionInfo
import ru.gorinih.familyshopper.data.remote.models.RemoteDictionary
import ru.gorinih.familyshopper.data.remote.models.toDictionaryRemoteTags
import ru.gorinih.familyshopper.data.remote.models.toListRemoteInfo
import ru.gorinih.familyshopper.data.remote.models.toShoppedList
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StoreRepository
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 30.06.2026
 */

class RemoteRepositoryImpl(
    private val client: HttpClient,
    private val store: StoreRepository
) : RemoteRepository {
    override suspend fun updateDictionaryWithVersion(updates: List<DictionaryRemoteTag>) {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return
        val maps = mutableMapOf<String, Any?>()
        updates.forEach {
            maps.putAll(it.toUpdateRemote())
        }
        client.request("/shared_data/$groupId.json") {
            method = HttpMethod.Patch
            contentType(ContentType.Application.Json)
            val jsonString = Json.encodeToString(AnyMapSerializer, maps)
            setBody(jsonString)
        }
    }

    override suspend fun updateListWithVersion(updates: List<ShoppedList>) {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return
        val maps = mutableMapOf<String, Any?>()
        updates.forEach {
            maps.putAll(it.toUpdateRemote())
        }
        client.request("/shared_data/$groupId.json") {
            method = HttpMethod.Patch
            contentType(ContentType.Application.Json)
            val jsonString = Json.encodeToString(AnyMapSerializer, maps)
            setBody(jsonString)
        }
    }

    override suspend fun deleteListWithVersion(listId: String) {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return
        val maps = listId.toDeleteRemote()
        client.request("/shared_data/$groupId.json") {
            method = HttpMethod.Patch
            contentType(ContentType.Application.Json)
            setBody(maps)
        }
    }

    override suspend fun setUserName() {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return
        val user = store.getClientUUID().toUpdateRemote(store.getUserName())
        client.request("/shared_data/$groupId.json") {
            method = HttpMethod.Patch
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    override suspend fun getUsersNames(): Map<String, String> {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        return try {
            val response = client.get("shared_data/$groupId/current_users.json")
            response.body<Map<String, String>?>() ?: emptyMap()
        } catch (_: Throwable) {
            emptyMap()
        }
    }

    override suspend fun getDictionariesVersions(): Map<String, Int> {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        return try {
            val response = client.get("shared_data/$groupId/dictionaries_versions.json")
            response.body<Map<String, Int>?>() ?: emptyMap()
        } catch (_: Throwable) {
            emptyMap()
        }
    }

    override suspend fun getAllDictionaries(): Map<String, DictionaryRemoteTag> {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        return try {
            val response = client.get("shared_data/$groupId/dictionaries.json")
            response.body<Map<String, RemoteDictionary>?>()?.map { (key, value) ->
                key to value.toDictionaryRemoteTags()
            }?.toMap() ?: emptyMap()
        } catch (_: Throwable) {
            emptyMap()
        }
    }

    override suspend fun getDictionaryById(tagId: String): DictionaryRemoteTag {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return DictionaryRemoteTag(
            tagId = "",
            tagVersion = 0,
            tagNames = emptyList()
        )
        return try {
            val response = client.get("shared_data/$groupId/dictionaries/$tagId.json")
            response.body<RemoteDictionary?>()?.toDictionaryRemoteTags() ?: DictionaryRemoteTag(
                tagId = "",
                tagVersion = 0,
                tagNames = emptyList()
            )
        } catch (_: Throwable) {
            DictionaryRemoteTag(tagId = "", tagVersion = 0, tagNames = emptyList())
        }
    }

    override suspend fun getListsVersions(): Map<String, ListRemoteInfo> {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        val response = client.get("shared_data/$groupId/current_lists_versions.json")
        return response.body<Map<String, ListVersionInfo>?>()
            ?.mapValues { it.value.toListRemoteInfo() } ?: emptyMap()
    }

    override suspend fun getAllCurrentLists(): Map<String, ShoppedList> {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return emptyMap()
        val response = client.get("shared_data/$groupId/current_lists.json")
        return response.body<Map<String, ListObject>?>()
            ?.mapValues { entity -> entity.value.toShoppedList() } ?: emptyMap()
    }

    override suspend fun getCurrentListById(listId: String): ShoppedList? {
        val groupId = store.getGroupUUID()
        if (groupId.isBlank()) return null
        val response = client.get("shared_data/$groupId/current_lists/$listId.json")
        return response.body<ListObject?>()?.toShoppedList()
    }
}