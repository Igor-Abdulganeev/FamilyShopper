package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

@Serializable
@Entity(tableName = "dictionary_ver")
data class DbDictionaryVersions(
    @PrimaryKey
    @ColumnInfo("tag_id")
    val tagId: String,
    @ColumnInfo("tag_version")
    val tagVersion: Int
)

fun DictionaryLocalVersionTag.toDbDictionaryVersions() =
    DbDictionaryVersions(
        tagId = this.tagId,
        tagVersion = this.tagVersion
    )