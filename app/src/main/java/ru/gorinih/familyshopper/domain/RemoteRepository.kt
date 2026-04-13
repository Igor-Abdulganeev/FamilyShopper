package ru.gorinih.familyshopper.domain

import ru.gorinih.familyshopper.domain.models.DictionaryRemoteTag
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 01.04.2026
 */

interface RemoteRepository {

    suspend fun getDictionariesVersions(): Map<String, Int>

    suspend fun getAllDictionaries(): Map<String, DictionaryRemoteTag>

    suspend fun getDictionaryById(tagId: String): DictionaryRemoteTag

    suspend fun updateDictionaryWithVersion(updates: List<DictionaryRemoteTag>)

    suspend fun getListsVersions(): Map<String, ListRemoteInfo>

    suspend fun updateListWithVersion(updates: List<ShoppedList>)

    suspend fun getAllCurrentLists(): Map<String, ShoppedList>

    suspend fun getCurrentListById(listId: String): ShoppedList?
    /*



         suspend fun deleteListWithVersion(groupId: String, updates: Map<String, Any?>): Response<UniUnit>
     */

}