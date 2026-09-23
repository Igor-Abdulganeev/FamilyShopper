package ru.gorinih.familyshopper.data.remote.models

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ListRemoteInfo

/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

@Serializable
data class ListVersionInfo(
    @ColumnInfo("listVersion")
    val listVersion: Int,
    @ColumnInfo("listLegend")
    val listLegend: Int,
    @ColumnInfo("listOwner")
    val listOwner: String,
    @ColumnInfo("listDatetime")
    val listDatetime: Long,
)

fun ListVersionInfo.toListRemoteInfo() =
    ListRemoteInfo(
        listVersion = this.listVersion,
        listLegend = this.listLegend,
        listOwner = this.listOwner,
        listDatetime = this.listDatetime
    )

