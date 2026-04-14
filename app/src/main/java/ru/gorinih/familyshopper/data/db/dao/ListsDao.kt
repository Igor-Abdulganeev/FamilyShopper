package ru.gorinih.familyshopper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.data.db.models.DbList
import ru.gorinih.familyshopper.data.db.models.DbListTags
import ru.gorinih.familyshopper.data.db.models.DbListVersions
import ru.gorinih.familyshopper.data.db.models.DbListVersionsOut

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Dao
interface ListsDao {
    //region работа со списками

    @Insert(entity = DbListVersions::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: DbListVersions)

    @Insert(entity = DbListTags::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListTags(listTags: List<DbListTags>)

    @Transaction
    suspend fun updateList(list: DbListVersions, tags:List<DbListTags>) {
        insertList(list)
        insertListTags(tags)
    }

    @Query("SELECT A.*, COALESCE(COUNT(B.tag_name), 0) AS \"count_tags\", COALESCE(SUM(B.tag_strike), 0) AS \"count_strike\", COALESCE(C.user_name,\"\") AS \"user_name\" FROM lists_ver AS A LEFT JOIN list_tags AS B ON A.list_id=B.list_id LEFT JOIN users_list AS C ON C.user_uuid = A.list_owner GROUP BY A.list_id ORDER BY A.list_datetime DESC")
    fun takeLists(): Flow<List<DbListVersionsOut>>

    @Query("SELECT * FROM lists_ver")
    suspend fun selectLists(): List<DbListVersions>

    @Query("SELECT A.*, B.tag_name, B.tag_strike, B.tag_comment, COALESCE(C.user_name,\"\") AS \"user_name\" FROM lists_ver AS A LEFT JOIN list_tags AS B ON B.list_id = A.list_id LEFT JOIN users_list AS C ON C.user_uuid = A.list_owner WHERE A.list_id=:listId ORDER BY B.tag_strike, B.tag_name")
    suspend fun takeList(listId: String): List<DbList>

    @Query("SELECT A.*, B.tag_name, B.tag_strike, B.tag_comment, COALESCE(C.user_name,\"\") AS \"user_name\" FROM lists_ver AS A LEFT JOIN list_tags AS B ON B.list_id = A.list_id LEFT JOIN users_list AS C ON C.user_uuid = A.list_owner WHERE A.list_id=:listId ORDER BY B.tag_strike, B.tag_name")
    fun flowList(listId: String): Flow<List<DbList>>

    //endregion


}