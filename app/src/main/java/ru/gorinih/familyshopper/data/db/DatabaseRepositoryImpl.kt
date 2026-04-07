package ru.gorinih.familyshopper.data.db

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.data.db.dao.DictionaryDao
import ru.gorinih.familyshopper.data.db.models.DbDeletedTags
import ru.gorinih.familyshopper.data.db.models.DbDictionary
import ru.gorinih.familyshopper.data.db.models.DbDictionaryVersions
import ru.gorinih.familyshopper.data.db.models.toDbDictionary
import ru.gorinih.familyshopper.data.db.models.toDbDictionaryVersions
import ru.gorinih.familyshopper.data.db.models.toDictionaryLocalTag
import ru.gorinih.familyshopper.data.db.models.toListOfDbDictionary
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

class DatabaseRepositoryImpl(
    private val dictionaryDao: DictionaryDao
) : DatabaseRepository {
    override suspend fun takeDictionariesVersions(): Map<String, Int> =
        dictionaryDao.takeDictionariesVersions()
            .associate { (tagId, tagVersion) -> tagId to tagVersion }

    override suspend fun takeKeysDictionaryForUpdate(): Map<String, Int> =
        dictionaryDao.takeKeysDictionaryForUpdate()

    override suspend fun updateDictionaries(dictionaries: List<DictionaryLocalVersionTag>) {
        val listVersions = mutableListOf<DbDictionaryVersions>()
        val listDictionaries = mutableListOf<DbDictionary>()
        val listDeleted = dictionaryDao.takeDeletedTags().map { it.tagName }
        for (dictionary in dictionaries) {
            val tagNames = dictionary.tagNames.filter { str -> str !in listDeleted }
            val finishDictionary = DictionaryLocalVersionTag(
                tagId = dictionary.tagId,
                tagVersion = dictionary.tagVersion,
                tagNames = tagNames
            )
            listVersions.add(finishDictionary.toDbDictionaryVersions())
            listDictionaries.addAll(finishDictionary.toListOfDbDictionary())
        }
        dictionaryDao.updateDictionary(
            versions = listVersions,
            dictionaries = listDictionaries
        )
    }

    override suspend fun takeUpdateTagsFromDictionary(tagId: String): Set<String> =
        dictionaryDao.takeUpdateTagsFromDictionary(tagId = tagId).toSet()

    override suspend fun updateDictionariesWithVersions(data: Map<String, Int>) {
        val deleteVersions = data.entries.map { DbDictionaryVersions(it.key, it.value) }
        dictionaryDao.apply {
            updateDictionariesVersions(deleteVersions)
            updateToRemoteTags()
            clearDeleteTags()
        }
    }

    /**
     * получить все тэги
     */
    @OptIn(FlowPreview::class)
    override fun takeDictionaries(): Flow<List<DictionaryLocalTag>> =
        dictionaryDao.takeDictionaries().map { list -> list.map { it.toDictionaryLocalTag() } }

    /**
     * сохранить новый тэг
     */
    override suspend fun addTag(tag: DictionaryLocalTag) {
        dictionaryDao.keepTag(tag.toDbDictionary())
    }

    override suspend fun deleteTag(tagId: String, tagName: String) {
        dictionaryDao.deleteTag(DbDeletedTags(tagId, tagName))
    }

}