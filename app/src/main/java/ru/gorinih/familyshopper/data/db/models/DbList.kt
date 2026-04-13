package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

data class DbList(
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
    val listTo: List<String>,
    @ColumnInfo("tag_name")
    val tagName: String?,
    @ColumnInfo("tag_strike")
    val tagStrike: Boolean?,
    @ColumnInfo("tag_comment")
    val tagComment: String?,
    @ColumnInfo("list_datetime")
    val listDatetime: Long,
)
