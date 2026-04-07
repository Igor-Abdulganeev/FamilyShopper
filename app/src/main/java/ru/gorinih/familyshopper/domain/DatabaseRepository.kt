package ru.gorinih.familyshopper.domain

import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag

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

    suspend fun addTag(tag: DictionaryLocalTag)

    suspend fun deleteTag(tagId: String, tagName: String)

}