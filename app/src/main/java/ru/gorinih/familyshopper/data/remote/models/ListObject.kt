package ru.gorinih.familyshopper.data.remote.models

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Serializable
data class ListObject(
    @ColumnInfo("list_id")
    val listId: String,
    @ColumnInfo("list_version")
    val listVersion: Int,
    @ColumnInfo("list_datetime")
    val listDateTime: Long,
    @ColumnInfo("list_name")
    val listName: String,
    @ColumnInfo("list_legend")
    val listLegend: Int,
    @ColumnInfo("list_owner")
    val listOwner: String,
    @ColumnInfo("to_owners")
    val listTo: List<String>? = emptyList(),
    @ColumnInfo("list_tags")
    val listTags: List<ListTagObject>? = emptyList(),
)

fun ListObject.toShoppedList() =
    ShoppedList(
        listId = this.listId,
        listName = this.listName,
        ownerUuid = this.listOwner,
        listVersion = this.listVersion,
        listLegend = this.listLegend,
        tagNames = this.listTags?.map { it.toShoppedItem() } ?: emptyList(),
        clientsUuid = this.listTo ?: emptyList(),
        dateTime = this.listDateTime,
        countTags = 0,
        countStrikes = 0
    )
