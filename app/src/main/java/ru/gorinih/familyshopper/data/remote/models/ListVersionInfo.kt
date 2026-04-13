package ru.gorinih.familyshopper.data.remote.models

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */
//@SerialName("list_id")
//val listId: String, - ключ map

@Serializable
data class ListVersionInfo(
    @ColumnInfo("list_version")
    val listVersion: Int,
    @ColumnInfo("list_legend")
    val listLegend: Int,
    @ColumnInfo("list_owner")
    val listOwner: String,
    @ColumnInfo("list_datetime")
    val listDatetime: Long,
)

fun ListVersionInfo.toListRemoteInfo() =
    ListRemoteInfo(
        listVersion = this.listVersion,
        listLegend = this.listLegend,
        listOwner = this.listOwner,
        listDatetime = this.listDatetime
    )

