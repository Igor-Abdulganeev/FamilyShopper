package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Serializable
@Entity(tableName = "lists_ver")
data class DbListVersions(
    @PrimaryKey
    @ColumnInfo("list_id")
    val listId: String,
    @ColumnInfo("list_version")
    val listVersion: Int,
    @ColumnInfo("list_name")
    val listName: String,
    @ColumnInfo("list_legend")
    val listLegend: Int,
    @ColumnInfo("list_owner")
    val listOwner: String,
    @ColumnInfo("to_owners")
    val listTo:List<String>,
    @ColumnInfo("list_datetime")
    val listDatetime: Long,
)

fun ShoppedList.toDbListVersions() =
    DbListVersions(
        listId = this.listId,
        listVersion = this.listVersion,
        listName = this.listName,
        listLegend = this.listLegend,
        listOwner = this.ownerUuid,
        listTo = this.clientsUuid,
        listDatetime = this.dateTime
    )

fun DbListVersions.toShoppedList() =
    ShoppedList(
        listId = this.listId,
        listName = this.listName,
        ownerUuid = this.listOwner,
        listVersion = this.listVersion,
        listLegend = this.listLegend,
        tagNames = emptyList(),
        clientsUuid = this.listTo,
        dateTime = this.listDatetime,
        countTags = 0,
        countStrikes = 0
    )
