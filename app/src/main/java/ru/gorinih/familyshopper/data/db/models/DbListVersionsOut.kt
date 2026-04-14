package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 10.04.2026
 */

data class DbListVersionsOut(
    @Embedded
    val listVersion: DbListVersions,
    @ColumnInfo(name = "count_tags")
    val countTags: Int,
    @ColumnInfo(name = "count_strike")
    val countStrike: Int,
    @ColumnInfo(name = "user_name")
    val userName: String
)

fun DbListVersionsOut.toShoppedList() =
    ShoppedList(
        listId = this.listVersion.listId,
        listName = this.listVersion.listName,
        ownerUuid = this.listVersion.listOwner,
        listVersion = this.listVersion.listVersion,
        listLegend = this.listVersion.listLegend,
        tagNames = emptyList(),
        usersUuid = this.listVersion.listTo.map { ShoppedUsers(userUuid = it, userName = "") },
        dateTime = this.listVersion.listDatetime,
        countTags = this.countTags,
        countStrikes = this.countStrike,
        userName = this.userName,
    )

