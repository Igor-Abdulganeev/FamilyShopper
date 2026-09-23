package ru.gorinih.familyshopper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.data.db.models.DbCompanionVersions
import ru.gorinih.familyshopper.data.db.models.DbDeletedTags
import ru.gorinih.familyshopper.data.db.models.DbDictionary
import ru.gorinih.familyshopper.data.db.models.DbDictionaryVersions

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */
// false = 0 true =1
@Dao
interface DictionaryDao {

    //region работа со словарями

    @Query("UPDATE dictionary SET need_update=0 WHERE need_update=1")
    suspend fun updateToRemoteTags()

    @Query("SELECT tag_id, tag_version FROM dictionary_ver")
    suspend fun takeDictionariesVersions(): List<DbCompanionVersions>

    @Query("SELECT DISTINCT tag_id FROM dictionary WHERE need_update=1 UNION SELECT DISTINCT tag_id FROM dictionary_deleted")
    suspend fun selectKeysDictionaryForUpdate(): List<String>

    @Query("SELECT tag_version FROM dictionary_ver WHERE tag_id=:tagId")
    suspend fun selectVersionFromKey(tagId: String): Int?

    suspend fun takeKeysDictionaryForUpdate(): Map<String, Int> {
        val keys = selectKeysDictionaryForUpdate()
        val result = mutableMapOf<String, Int>()
        for (key in keys) {
            selectVersionFromKey(key)?.let { ver ->
                result[key] = ver
            } ?: run { result[key] = 0 }
        }
        return result
    }

    @Query("SELECT tag_name FROM dictionary WHERE tag_id=:tagId") // AND need_update=1 - передаем ВСЁ т.к. нет обновления, записи что требуется на сервер передать
    suspend fun takeUpdateTagsFromDictionary(tagId: String): List<String>

    @Query("DELETE FROM dictionary WHERE tag_id=:tagId AND need_update=0")
    suspend fun deleteDictionary(tagId: String)

    @Insert(entity = DbDictionary::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDictionaries(dictionaries: List<DbDictionary>)

    @Insert(entity = DbDictionaryVersions::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDictionariesVersions(dictionary: List<DbDictionaryVersions>)

    @Transaction
    suspend fun updateDictionary(
        versions: List<DbDictionaryVersions>,
        dictionaries: List<DbDictionary>
    ) {
        val deleteVersions = versions.map { DbDictionaryVersions(it.tagId, it.tagVersion) }
        updateDictionariesVersions(deleteVersions)
        for (ver in versions) {
            deleteDictionary(ver.tagId)
        }
        updateDictionaries(dictionaries = dictionaries)
    }

    @Query("SELECT * FROM dictionary ORDER BY tag_id")
    fun takeDictionaries(): Flow<List<DbDictionary>>

    @Query("SELECT * FROM dictionary ORDER BY tag_id")
    suspend fun selectDictionaries(): List<DbDictionary>

    @Insert(entity = DbDictionary::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun keepTag(tag: DbDictionary)

    @Insert(entity = DbDictionary::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun keepTags(tags: List<DbDictionary>)

    @Query("DELETE FROM dictionary WHERE tag_name=:tagName")
    suspend fun deleteTagByName(tagName: String)

    @Insert(entity = DbDeletedTags::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeletedTag(tagName: DbDeletedTags)

    @Transaction
    suspend fun deleteTag(tag: DbDeletedTags) {
        insertDeletedTag(tag)
        deleteTagByName(tag.tagName)
    }

    @Query("SELECT * FROM dictionary_deleted")
    suspend fun takeDeletedTags(): List<DbDeletedTags>

    @Query("DELETE FROM dictionary_deleted")
    suspend fun clearDeleteTags()

    @Query("DELETE FROM dictionary_ver WHERE tag_id=:tagId")
    suspend fun deleteDictionaryVersion(tagId: String)

    //endregion

}