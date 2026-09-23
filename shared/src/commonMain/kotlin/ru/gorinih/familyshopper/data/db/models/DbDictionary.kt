package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.DictionaryLocalTag
import ru.gorinih.familyshopper.domain.models.DictionaryLocalVersionTag

/**
 * Created by Igor Abdulganeev on 05.04.2026
 */

@Serializable
@Entity(tableName = "dictionary", primaryKeys = ["tag_id", "tag_name"])
data class DbDictionary(
    @ColumnInfo("tag_id")
    val tagId: String,
    @ColumnInfo("tag_name")
    val tagName: String,
    @ColumnInfo("need_update")
    val needUpdate: Boolean = false // обновлять на сервер не надо отсылать
)

fun DictionaryLocalTag.toDbDictionary() =
    DbDictionary(
        tagId = this.tagId,
        tagName = this.tagName,
        needUpdate = this.needUpdate
    )

fun DictionaryLocalVersionTag.toListOfDbDictionary() =
    this.tagNames.map {
        DbDictionary(
            tagId = this.tagId,
            tagName = it
        )
    }

fun DbDictionary.toDictionaryLocalTag() =
    DictionaryLocalTag(
        tagId = this.tagId,
        tagName = this.tagName,
        needUpdate = this.needUpdate
    )