package ru.gorinih.familyshopper.domain

import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

interface DatabaseRepository {

    suspend fun takeDictionariesVersions(): Map<String,Int>

    suspend fun takeKeysDictionaryForUpdate(): Map<String, Int>

    suspend fun updateDictionaries(dictionaries: List<DictionaryLocalVersionTag>)

    suspend fun takeUpdateTagsFromDictionary(tagId: String): Set<String>

    suspend fun updateDictionariesWithVersions(data: Map<String, Int>)

    fun takeDictionaries(): Flow<List<DictionaryLocalTag>>

    suspend fun getDictionaryTags(): List<String>

    suspend fun addTag(tag: DictionaryLocalTag)

    suspend fun addTags(tags: List<DictionaryLocalTag>)

    suspend fun deleteTag(tagId: String, tagName: String)

    suspend fun updateList(data: ShoppedList)

    fun takeLists(): Flow<List<ShoppedList>>

    suspend fun takeListsWithVersions(): Map<String,ShoppedList>

    suspend fun takeList(listId: String): ShoppedList

    fun observeList(listId: String): Flow<ShoppedList>

    fun takeUsers(): Flow<List<ShoppedUsers>>

    suspend fun takeUser(userUuid: String): ShoppedUsers?

    suspend fun keepUsers(users: List<ShoppedUsers>)

    suspend fun keepUser(user: ShoppedUsers)
}