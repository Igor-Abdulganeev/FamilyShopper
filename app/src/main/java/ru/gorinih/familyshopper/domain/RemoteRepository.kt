package ru.gorinih.familyshopper.domain

import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

interface RemoteRepository {

    suspend fun getDictionariesVersions(): Map<String, Int>

    suspend fun getAllDictionaries(): Map<String, DictionaryRemoteTag>

    suspend fun getDictionaryById(tagId: String): DictionaryRemoteTag

    suspend fun updateDictionaryWithVersion(
        updates: List<DictionaryRemoteTag>
    )

    /*
        suspend fun getListsVersions(groupId: String): Map<String, ListVersionInfo>?

        suspend fun getAllCurrentLists(groupId: String): Map<String, CurrentList>?

        suspend fun deleteListWithVersion(groupId: String, updates: Map<String, Any?>): Response<Unit>

        suspend fun updateListWithVersion(groupId: String, updates: Map<String, Any>): Response<Unit>
    */

}