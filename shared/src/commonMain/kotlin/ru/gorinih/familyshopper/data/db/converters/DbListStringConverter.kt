package ru.gorinih.familyshopper.data.db.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

/**
 * список в Room одной строкой
 */

object DbListStringConverter {
    @TypeConverter
    fun toList(value: String): List<String> = Json.decodeFromString(value)

    @TypeConverter
    fun fromList(value: List<String>): String = Json.encodeToString(value)
}