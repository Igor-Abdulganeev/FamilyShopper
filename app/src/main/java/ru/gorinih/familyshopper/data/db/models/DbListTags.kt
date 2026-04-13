package ru.gorinih.familyshopper.data.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import kotlinx.serialization.Serializable
import ru.gorinih.familyshopper.domain.models.ShoppedList

/**
 * Created by Igor Abdulganeev on 09.04.2026
 */

@Serializable
@Entity(tableName = "list_tags", primaryKeys = ["list_id", "tag_name"], foreignKeys = [
    ForeignKey(
        entity = DbListVersions::class,
        parentColumns = ["list_id"],
        childColumns = ["list_id"],
        onDelete = CASCADE
    )
])
data class DbListTags(
    @ColumnInfo("list_id")
    val listId: String,
    @ColumnInfo("tag_name")
    val tagName: String,
    @ColumnInfo("tag_strike")
    val tagStrike: Boolean,
    @ColumnInfo("tag_comment")
    val tagComment: String,
)

fun ShoppedList.toListDbListTags(): List<DbListTags> =
    this.tagNames.map { tag ->
        DbListTags(
            listId = this.listId,
            tagName = tag.tagName,
            tagStrike = tag.isStrike,
            tagComment = tag.tagComment
        )
    }
