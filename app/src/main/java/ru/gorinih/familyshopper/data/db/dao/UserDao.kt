package ru.gorinih.familyshopper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.gorinih.familyshopper.data.db.models.DbUsers

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */
@Dao
interface UserDao {

    @Insert(entity = DbUsers::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsers(list: List<DbUsers>)

   @Insert(entity = DbUsers::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun replaceUsers(list: List<DbUsers>)

    @Insert(entity = DbUsers::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: DbUsers)

    @Query("SELECT * FROM users_list")
    fun takeUsers(): Flow<List<DbUsers>>

    @Query("SELECT * FROM users_list where user_uuid=:userUuid")
    suspend fun takeUser(userUuid: String): DbUsers?

    @Query("DELETE FROM users_list where user_uuid=:userUuid")
    suspend fun deleteUser(userUuid: String)
}