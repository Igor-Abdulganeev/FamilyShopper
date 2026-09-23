package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 13.04.2026
 */

@Entity(tableName = "users_list")
@Serializable
data class DbUsers(
    @PrimaryKey
    @ColumnInfo(name = "user_uuid")
    val userUuid: String,
    @ColumnInfo(name = "user_name")
    val userName: String,
)

fun DbUsers.toShoppedUsers() =
    ShoppedUsers(
        userUuid = this.userUuid,
        userName = this.userName
    )

fun ShoppedUsers.toDbUsers() =
    DbUsers(
        userUuid = this.userUuid,
        userName = this.userName
    )