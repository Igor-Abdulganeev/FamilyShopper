package ru.gorinih.familyshopper.data.db

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.gorinih.familyshopper.data.db.dao.DictionaryDao
import ru.gorinih.familyshopper.data.db.dao.ListsDao
import ru.gorinih.familyshopper.data.db.dao.UserDao
import ru.gorinih.familyshopper.data.db.models.DbDeletedTags
import ru.gorinih.familyshopper.data.db.models.DbDictionary
import ru.gorinih.familyshopper.data.db.models.DbDictionaryVersions
import ru.gorinih.familyshopper.data.db.models.DbListTags
import ru.gorinih.familyshopper.data.db.models.DbListVersions
import ru.gorinih.familyshopper.data.db.models.toDbDictionary
import ru.gorinih.familyshopper.data.db.models.toDbDictionaryVersions
import ru.gorinih.familyshopper.data.db.models.toDbListVersions
import ru.gorinih.familyshopper.data.db.models.toDbUsers
import ru.gorinih.familyshopper.data.db.models.toDictionaryLocalTag
import ru.gorinih.familyshopper.data.db.models.toListDbListTags
import ru.gorinih.familyshopper.data.db.models.toListOfDbDictionary
import ru.gorinih.familyshopper.data.db.models.toShoppedList
import ru.gorinih.familyshopper.data.db.models.toShoppedUsers
import ru.gorinih.familyshopper.domain.DatabaseRepository
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag
import ru.gorinih.familyshopper.domain.models.ShoppedItem
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

class DatabaseRepositoryImpl(
    private val dictionaryDao: DictionaryDao,
    private val listsDao: ListsDao,
    private val userDao: UserDao,
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

    override suspend fun addTags(tags: List<DictionaryLocalTag>) {
        dictionaryDao.keepTags(tags.map { it.toDbDictionary() })
    }

    override suspend fun deleteTag(tagId: String, tagName: String) {
        dictionaryDao.deleteTag(DbDeletedTags(tagId, tagName))
    }

    /**
     * сохранение списка
     */
    override suspend fun updateList(data: ShoppedList) {
        val version: DbListVersions = data.toDbListVersions()
        val tags: List<DbListTags> = data.toListDbListTags()
        listsDao.updateList(list = version, tags = tags)

    }

    /**
     * получение статического списка
     */
    override suspend fun getDictionaryTags(): List<String> =
        dictionaryDao.selectDictionaries().map { it.tagName }

    /**
     * получение всех локальных списков
     */
    override fun takeLists(): Flow<List<ShoppedList>> =
        listsDao.takeLists().map { list -> list.map { item -> item.toShoppedList() } }

    /**
     * получение слепка локальных списков
     */
    override suspend fun takeListsWithVersions(): Map<String, ShoppedList> =
        listsDao.selectLists().map { it.toShoppedList() }.associateBy { list -> list.listId }

    /**
     * получение подробного списка
     */
    override suspend fun takeList(listId: String): ShoppedList =
        listsDao.takeList(listId = listId).groupBy { dbList -> dbList.listId }
            .map { (_, values) ->
                val firstItem = values.first()
                ShoppedList(
                    listId = firstItem.listId,
                    listName = firstItem.listName,
                    ownerUuid = firstItem.listOwner,
                    listVersion = firstItem.listVersion,
                    listLegend = firstItem.listLegend,
                    usersUuid = firstItem.listTo.map { ShoppedUsers(userUuid = it, userName = "") },
                    dateTime = firstItem.listDatetime,
                    tagNames = values.mapNotNull { value ->
                        if (value.tagName == null || value.tagStrike == null || value.tagComment == null) null else
                            ShoppedItem(
                                tagId = value.tagName.first().uppercase(),
                                tagName = value.tagName,
                                isStrike = value.tagStrike,
                                tagComment = value.tagComment
                            )
                    },
                    countTags = values.mapNotNull { value -> value.tagName }.size,
                    countStrikes = values.filter { value -> value.tagStrike == true }.size,
                    userName = firstItem.userName
                )
            }.first()

    override fun observeList(listId: String): Flow<ShoppedList> =
        listsDao.flowList(listId = listId).map { listsDao ->
            listsDao.groupBy { dbList -> dbList.listId }
                .map { (_, values) ->
                    val firstItem = values.first()
                    ShoppedList(
                        listId = firstItem.listId,
                        listName = firstItem.listName,
                        ownerUuid = firstItem.listOwner,
                        listVersion = firstItem.listVersion,
                        listLegend = firstItem.listLegend,
                        usersUuid = firstItem.listTo.map {
                            ShoppedUsers(
                                userUuid = it,
                                userName = ""
                            )
                        },
                        dateTime = firstItem.listDatetime,
                        tagNames = values.mapNotNull { value ->
                            if (value.tagName == null || value.tagStrike == null || value.tagComment == null) null else
                                ShoppedItem(
                                    tagId = value.tagName.first().uppercase(),
                                    tagName = value.tagName,
                                    isStrike = value.tagStrike,
                                    tagComment = value.tagComment
                                )
                        },
                        countTags = values.mapNotNull { value -> value.tagName }.size,
                        countStrikes = values.filter { value -> value.tagStrike == true }.size,
                        userName = firstItem.userName
                    )
                }.first()
        }

    override fun takeUsers(): Flow<List<ShoppedUsers>> =
        userDao.takeUsers().map { list -> list.map { it.toShoppedUsers() } }

    override suspend fun takeUser(userUuid: String): ShoppedUsers? =
        userDao.takeUser(userUuid = userUuid)?.toShoppedUsers()

    override suspend fun keepUsers(users: List<ShoppedUsers>) {
        userDao.insertUsers(users.map { it.toDbUsers() })
    }

    override suspend fun keepUser(user: ShoppedUsers) {
        userDao.insertUser(user.toDbUsers())
    }

    override suspend fun deleteDictionaryVersion(tagId: String) {
        dictionaryDao.deleteDictionaryVersion(tagId = tagId)
    }
}