package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.Serializable

/**
 * Created by Igor Abdulganeev on 06.04.2026
 */

@Serializable
@Entity(tableName = "dictionary_deleted", primaryKeys = ["tag_id", "tag_name"])
data class DbDeletedTags(
    @ColumnInfo(name = "tag_id")
    val tagId: String,
    @ColumnInfo(name = "tag_name")
    val tagName: String
)
