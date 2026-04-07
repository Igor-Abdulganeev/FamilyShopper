package ru.gorinih.familyshopper.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import ru.gorinih.familyshopper.data.db.dao.DictionaryDao
import ru.gorinih.familyshopper.data.db.models.DbDeletedTags
import ru.gorinih.familyshopper.data.db.models.DbDictionary
import ru.gorinih.familyshopper.data.db.models.DbDictionaryVersions

/**
 * Created by Igor Abdulganeev on 04.04.2026
 */
@Database(
    entities = [
        DbDictionary::class,
        DbDictionaryVersions::class,
        DbDeletedTags::class,
    ],
    exportSchema = false,
    version = 1
)
abstract class ShopperDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}

fun shopperDatabaseBuilder(context: Context): ShopperDatabase {
    val contextApplication = context.applicationContext
    val dbFile = contextApplication.getDatabasePath("family_shopper.db")
    return Room.databaseBuilder(
        context = contextApplication,
        klass = ShopperDatabase::class.java,
        name = dbFile.absolutePath
    ).setQueryCoroutineContext(Dispatchers.IO)
        .build()
}