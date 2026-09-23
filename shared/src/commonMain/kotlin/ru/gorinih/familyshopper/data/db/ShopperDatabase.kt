package ru.gorinih.familyshopper.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import ru.gorinih.familyshopper.data.db.converters.DbListStringConverter
import ru.gorinih.familyshopper.data.db.dao.DictionaryDao
import ru.gorinih.familyshopper.data.db.dao.ListsDao
import ru.gorinih.familyshopper.data.db.dao.UserDao
import ru.gorinih.familyshopper.data.db.models.DbDeletedTags
import ru.gorinih.familyshopper.data.db.models.DbDictionary
import ru.gorinih.familyshopper.data.db.models.DbDictionaryVersions
import ru.gorinih.familyshopper.data.db.models.DbListTags
import ru.gorinih.familyshopper.data.db.models.DbListVersions
import ru.gorinih.familyshopper.data.db.models.DbUsers

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */
@Database(
    entities = [
        DbDictionary::class,
        DbDictionaryVersions::class,
        DbDeletedTags::class,
        DbListVersions::class,
        DbListTags::class,
        DbUsers::class
    ],
    exportSchema = true,
    version = 1
)
@TypeConverters(DbListStringConverter::class)
@ConstructedBy(ShopperDatabaseConstructor::class)
abstract class ShopperDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun listDao(): ListsDao
    abstract fun userDao(): UserDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ShopperDatabaseConstructor : RoomDatabaseConstructor<ShopperDatabase>
