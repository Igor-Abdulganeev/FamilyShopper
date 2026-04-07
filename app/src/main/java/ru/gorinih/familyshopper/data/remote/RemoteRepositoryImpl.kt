package ru.gorinih.familyshopper.data.remote

import android.util.Log
import ru.gorinih.familyshopper.data.remote.models.RemoteDictionary
import ru.gorinih.familyshopper.data.remote.models.toDictionaryRemoteTags
import ru.gorinih.familyshopper.domain.RemoteRepository
import ru.gorinih.familyshopper.domain.StorageRepository
import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

private const val PATH_DICTIONARY = "dictionaries/"
private const val PATH_DICTIONARY_VERSION = "dictionaries_versions/"

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
        Log.e("GINES","updates=$updates")
        val uuid = pref.getGroupUUID()
        val maps = mutableMapOf<String, Any>()
        updates.forEach {
            maps.putAll(it.toUpdateRemote())
        }
        Log.e("GINES","uuid = ${uuid} / maps=$maps")
        remoteApi.updateDictionaryWithVersion(
            groupId = uuid,
            updates = maps
        )
    }
}

fun DictionaryRemoteTag.toUpdateRemote(): Map<String, Any> = mapOf(
    "$PATH_DICTIONARY_VERSION${this.tagId}" to this.tagVersion,
    "$PATH_DICTIONARY${this.tagId}" to RemoteDictionary(
        tagVersion = this.tagVersion,
        tagId = this.tagId,
        tagNames = this.tagNames
    )
)
