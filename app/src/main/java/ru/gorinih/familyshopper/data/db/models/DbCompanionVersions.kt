package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo

/**
 * структура для списка версий
 */

data class DbCompanionVersions(
    @ColumnInfo(name = "tag_id")
    val tagId: String,
    @ColumnInfo(name = "tag_version")
    val tagVersion: Int
)
