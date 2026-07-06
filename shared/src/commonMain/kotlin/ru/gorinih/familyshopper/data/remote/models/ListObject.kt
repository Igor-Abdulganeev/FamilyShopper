package ru.gorinih.familyshopper.data.remote.models

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.LegendList
import ru.gorinih.familyshopper.domain.models.ShoppedList
import ru.gorinih.familyshopper.domain.models.ShoppedUsers

/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

@Serializable
data class ListObject(
    @ColumnInfo("listId")
    val listId: String,
    @ColumnInfo("listVersion")
    val listVersion: Int,
    @ColumnInfo("listDateTime")
    val listDateTime: Long,
    @ColumnInfo("listName")
    val listName: String,
    @ColumnInfo("listLegend")
    val listLegend: Int,
    @ColumnInfo("listOwner")
    val listOwner: String,
    @ColumnInfo("listTo")
    val listTo: List<String>? = emptyList(),
    @ColumnInfo("listTags")
    val listTags: List<ListTagObject>? = emptyList(),
)

fun ListObject.toShoppedList() =
    ShoppedList(
        listId = this.listId,
        listName = this.listName,
        ownerUuid = this.listOwner,
        listVersion = this.listVersion,
        listLegend = LegendList.entries.firstOrNull { it.listId == this.listLegend }
            ?: LegendList.ALL,
        tagNames = this.listTags?.map { it.toShoppedItem() } ?: emptyList(),
        usersUuid = this.listTo?.map { ShoppedUsers(userUuid = it, userName = "") } ?: emptyList(),
        dateTime = this.listDateTime,
        countTags = 0,
        countStrikes = 0,
        userName = ""
    )
